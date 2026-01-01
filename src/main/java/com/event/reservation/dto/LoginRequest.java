package com.event.reservation.dto;

public record LoginRequest(
        String email,
        String password
) {}
