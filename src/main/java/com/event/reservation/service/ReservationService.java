package com.event.reservation.service;

import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.ReservationStatus; // <--- ADDED THIS
import java.time.LocalDateTime; // <--- ADDED THIS
import java.util.List;

public interface ReservationService {

    Reservation createReservation(Long eventId, Long userId, int nombrePlaces, String commentaire);

    Reservation confirmerReservation(Long reservationId);

    Reservation annulerReservation(Long reservationId);

    Reservation getByCode(String code);

    List<Reservation> getReservationsByUser(Long userId);

    List<Reservation> findByEventId(Long eventId);

    double getTotalAmountByUser(Long userId);

    List<Reservation> getUpcomingReservations(Long userId);

    long countReservationsByUser(Long userId);

    void annuler(Long id);

    // --- ADMIN METHODS ---

    // 1. Advanced Search
    List<Reservation> getAllReservations(String keyword, ReservationStatus status, LocalDateTime start, LocalDateTime end);

    // 2. Stats methods
    long countReservations(ReservationStatus status);

    Double getTotalRevenue();

    // 3. Export method
    String generateCSVExport();
}