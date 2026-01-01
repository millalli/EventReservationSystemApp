package com.event.reservation.entity;

public enum Role {
    ADMIN,
    ORGANIZER,
    CLIENT;
    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isOrganizer() {
        return this == ORGANIZER;
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}
