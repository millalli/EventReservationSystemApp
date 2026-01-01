package com.event.reservation.service;

import com.event.reservation.entity.Role;
import com.event.reservation.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(User user);     // Inscription
    User authenticate(String email, String password); // Login

    User updateProfile(Long id, User user);

    void changePassword(Long id, String oldPassword, String newPassword);

    void activateUser(Long id);
    void deactivateUser(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAllActiveUsers();
    // Add these methods
    List<User> searchUsers(String keyword, Role role, Boolean isEnabled);
    void toggleUserStatus(Long userId); // Activate/Deactivate
    void updateUserRole(Long userId, Role newRole);
}
