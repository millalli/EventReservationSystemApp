package com.event.reservation.repository;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.User;
import com.event.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // ✔ Trouver les réservations d’un utilisateur
    List<Reservation> findByUtilisateur(User utilisateur);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.utilisateur WHERE r.evenement.id = :eventId")
    List<Reservation> findByEvenementId(@Param("eventId") Long eventId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.evenement WHERE r.utilisateur.id = :userId")
    List<Reservation> findByUtilisateurId(@Param("userId") Long userId);

    // ✔ Trouver les réservations d’un événement avec un statut précis
    List<Reservation> findByEvenementAndStatut(Event evenement, ReservationStatus statut);

    // ✔ Compter le nombre total de places réservées
    Integer countByEvenement(Event evenement);

    // ✔ Trouver une réservation par code
    Optional<Reservation> findByCodeReservation(String codeReservation);

    // ✔ Trouver les réservations entre deux dates
    List<Reservation> findByDateReservationBetween(LocalDateTime start, LocalDateTime end);

    // ✔ Réservations confirmées d’un utilisateur
    List<Reservation> findByUtilisateurAndStatut(User utilisateur, ReservationStatus statut);

    // --- STATISTICS & COUNTS ---

    long countByStatut(ReservationStatus status);

    // Fixed: Standardized to 'montantTotal'
    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r WHERE r.statut = 'CONFIRMEE'")
    Double calculateTotalRevenue();

    // Count total reservations for this organizer's events
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.evenement.organisateur.id = :organisateurId")
    long countTotalReservationsForOrganizer(@Param("organisateurId") Long organisateurId);

    // Calculate total revenue for organizer
    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r WHERE r.evenement.organisateur.id = :organisateurId AND r.statut = 'CONFIRMEE'")
    Double calculateTotalRevenueForOrganizer(@Param("organisateurId") Long organisateurId);

    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r WHERE r.evenement.id = :eventId")
    long countTotalPlacesByEvent(@Param("eventId") Long eventId);

    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r WHERE r.utilisateur.id = :userId")
    double sumMontantTotalByUser(@Param("userId") Long userId);

    long countByEvenementIdAndStatutNot(Long eventId, ReservationStatus status);

    // NEW: Count total reservations for user
    long countByUtilisateurId(Long userId);

    // NEW: Find upcoming reservations for a specific user
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :userId AND r.evenement.dateDebut > CURRENT_TIMESTAMP ORDER BY r.evenement.dateDebut ASC")
    List<Reservation> findUpcomingReservationsByUserId(@Param("userId") Long userId);

    // --- ADVANCED SEARCH (FIXED) ---
    // Fixes: changed 'r.client' to 'r.utilisateur', 'r.code' to 'r.codeReservation', 'r.prixTotal' to 'r.montantTotal'
    @Query("""
        SELECT r FROM Reservation r
        WHERE (:keyword IS NULL OR 
               LOWER(r.codeReservation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.evenement.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.utilisateur.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.utilisateur.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:status IS NULL OR r.statut = :status)
        AND (CAST(:startDate AS timestamp) IS NULL OR r.dateReservation >= :startDate)
        AND (CAST(:endDate AS timestamp) IS NULL OR r.dateReservation <= :endDate)
        ORDER BY r.dateReservation DESC
    """)
    List<Reservation> searchGlobalReservations(@Param("keyword") String keyword,
                                               @Param("status") ReservationStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}