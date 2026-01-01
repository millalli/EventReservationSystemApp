package com.event.reservation.service.impl;

import com.event.reservation.entity.Role;
import com.event.reservation.entity.User;
import com.event.reservation.repository.UserRepository;
import com.event.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDateInscription(LocalDateTime.now());
        user.setActif(true);

        return userRepository.save(user);
    }

    @Override
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect !");
        }

        return user;
    }

    @Override
    public User updateProfile(Long id, User updated) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setNom(updated.getNom());
        user.setPrenom(updated.getPrenom());
        user.setTelephone(updated.getTelephone());

        return userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setActif(true);
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setActif(false);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActif)
                .toList();
    }
    @Override
    public List<User> searchUsers(String keyword, Role role, Boolean isEnabled) {
        return userRepository.searchUsers(keyword, role, isEnabled);
    }

    @Override
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActif(!user.isEnabled()); // Flip the status
        userRepository.save(user);
    }

    @Override
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(newRole);
        userRepository.save(user);
    }
}

