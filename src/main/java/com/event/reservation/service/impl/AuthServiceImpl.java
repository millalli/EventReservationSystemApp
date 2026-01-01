package com.event.reservation.service.impl;

import com.event.reservation.dto.AuthResponse;
import com.event.reservation.dto.LoginRequest;
import com.event.reservation.dto.RegisterRequest;
import com.event.reservation.entity.Role;
import com.event.reservation.entity.User;
import com.event.reservation.exception.BadRequestException;
import com.event.reservation.repository.UserRepository;
import com.event.reservation.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email déjà utilisé !");
        }

        User user = new User();
        user.setNom(request.nom());
        user.setPrenom(request.prenom());
        user.setEmail(request.email());
        user.setTelephone(request.telephone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CLIENT);
        user.setActif(true);
        user.setDateInscription(LocalDate.now().atStartOfDay());

        userRepository.save(user);

        return new AuthResponse("Inscription réussie", null);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Mot de passe incorrect");
        }

        return new AuthResponse("Connexion réussie", null);
    }
}
