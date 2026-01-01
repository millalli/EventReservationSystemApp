package com.event.reservation.service;

import com.event.reservation.dto.LoginRequest;
import com.event.reservation.dto.RegisterRequest;
import com.event.reservation.dto.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
