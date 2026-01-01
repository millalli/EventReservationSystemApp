package com.event.reservation.service.impl;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.ReservationStatus;
import com.event.reservation.entity.User;
import com.event.reservation.repository.EventRepository;
import com.event.reservation.repository.ReservationRepository;
import com.event.reservation.repository.UserRepository;
import com.event.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Reservation createReservation(Long eventId, Long userId, int nombrePlaces, String commentaire) {
        // 1. Find Event and User
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Calculate remaining places dynamically
        // We use the repository method we created earlier to count occupied seats
        long placesPrises = reservationRepository.countTotalPlacesByEvent(eventId);

        long placesRestantes = event.getCapaciteMax() - placesPrises;

        // 3. Check if enough space is available
        if (placesRestantes < nombrePlaces) {
            throw new RuntimeException("Not enough places available. Only " + placesRestantes + " left.");
        }

        // 4. Create and Save Reservation
        Reservation reservation = new Reservation();
        reservation.setEvenement(event);
        reservation.setUtilisateur(user);
        reservation.setNombrePlaces(nombrePlaces);
        reservation.setCommentaire(commentaire);
        reservation.setStatut(ReservationStatus.EN_ATTENTE);

        return reservationRepository.save(reservation);
    }
    @Override
    public Reservation confirmerReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatut(ReservationStatus.CONFIRMEE);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation annulerReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatut(ReservationStatus.ANNULEE);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation getByCode(String code) {
        return reservationRepository.findByCodeReservation(code)
                .orElseThrow(() -> new RuntimeException("Reservation not found with code: " + code));
    }

    @Override
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUtilisateurId(userId);
    }

    @Override
    public List<Reservation> findByEventId(Long eventId) {
        return reservationRepository.findByEvenementId(eventId);
    }

    @Override
    public double getTotalAmountByUser(Long userId) {
        return reservationRepository.sumMontantTotalByUser(userId);
    }

    @Override
    public List<Reservation> getUpcomingReservations(Long userId) {
        return reservationRepository.findUpcomingReservationsByUserId(userId);
    }

    @Override
    public long countReservationsByUser(Long userId) {
        return reservationRepository.countByUtilisateurId(userId);
    }

    @Override
    public void annuler(Long id) {
        annulerReservation(id);
    }

    // --- IMPLEMENTATION OF NEW ADMIN METHODS ---

    @Override
    public List<Reservation> getAllReservations(String keyword, ReservationStatus status, LocalDateTime start, LocalDateTime end) {
        // Calls the advanced search query in Repository
        return reservationRepository.searchGlobalReservations(keyword, status, start, end);
    }

    @Override
    public long countReservations(ReservationStatus status) {
        return reservationRepository.countByStatut(status);
    }

    @Override
    public Double getTotalRevenue() {
        return reservationRepository.calculateTotalRevenue();
    }

    @Override
    public String generateCSVExport() {
        // Fetch all reservations (or you could filter them if needed)
        List<Reservation> all = reservationRepository.findAll();
        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("ID,Code,Event,Client,Date,Status,Total Amount\n");

        for (Reservation r : all) {
            csv.append(r.getId()).append(",")
                    .append(r.getCodeReservation()).append(",") // Fixed: getCodeReservation
                    .append("\"").append(r.getEvenement().getTitre()).append("\",") // Escape quotes for titles
                    .append(r.getUtilisateur().getNom()).append(" ").append(r.getUtilisateur().getPrenom()).append(",") // Fixed: getUtilisateur
                    .append(r.getDateReservation()).append(",")
                    .append(r.getStatut()).append(",")
                    .append(r.getMontantTotal()) // Fixed: getMontantTotal
                    .append("\n");
        }
        return csv.toString();
    }
}