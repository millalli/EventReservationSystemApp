package com.event.reservation.dto;

public record RegisterRequest(
        String nom,
        String prenom,
        String telephone,
        String email,
        String password
) {}
