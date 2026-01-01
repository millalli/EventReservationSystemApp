package com.event.reservation.service;

import com.event.reservation.entity.EventStatus;
import com.event.reservation.entity.ReservationStatus;
import com.event.reservation.entity.Role;
import com.event.reservation.repository.EventRepository;
import com.event.reservation.repository.ReservationRepository;
import com.event.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, Long> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("Total", userRepository.count());
        stats.put("Clients", userRepository.countByRole(Role.CLIENT));
        stats.put("Organizers", userRepository.countByRole(Role.ORGANIZER));
        return stats;
    }

    public Map<String, Long> getEventStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("Total", eventRepository.count());
        // Assuming your enum is PUBLISHED, DRAFT, etc.
        stats.put("Published", eventRepository.countByStatut(EventStatus.PUBLIE));
        stats.put("Drafts", eventRepository.countByStatut(EventStatus.BROUILLON));
        return stats;
    }

    public long getTotalReservations() {
        return reservationRepository.count();
    }

    public Double getTotalRevenue() {
        Double revenue = reservationRepository.calculateTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }
}