package com.event.reservation.security;

import com.event.reservation.entity.User;
import com.event.reservation.repository.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class SecurityService {

    private final AuthenticationContext authenticationContext;
    private final UserRepository userRepository;

    public SecurityService(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.authenticationContext = authenticationContext;
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByEmail(userDetails.getUsername())
                        .orElse(null))
                .orElse(null);
    }

    public void logout() {
        authenticationContext.logout();
    }
}