package com.event.reservation.entity;

import com.event.reservation.entity.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- L'utilisateur qui a réservé ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User utilisateur;

    // --- L'événement réservé ---
    // Change LAZY to EAGER to fix the Internal Error / LazyInitializationException
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event evenement;

    // --- Nombre de places ---
    @Column(nullable = false)
    private Integer nombrePlaces;

    // --- Montant total (calculé automatiquement) ---
    private Double montantTotal;

    // --- Date de réservation ---
    private LocalDateTime dateReservation;

    // --- Statut ---
    @Enumerated(EnumType.STRING)
    private ReservationStatus statut;

    // --- Code réservation unique ex : EVT-12345 ---
    @Column(nullable = false, unique = true)
    private String codeReservation;

    // --- Commentaire optionnel ---
    private String commentaire;

    // --- Méthodes utilitaires ---

    /** Calcul automatique du montant total */
    public void calculerMontantTotal() {
        if (evenement != null && nombrePlaces != null) {
            this.montantTotal = evenement.getPrixUnitaire() * nombrePlaces;
        }
    }

    /** Génération du code réservation unique */
    public void genererCodeReservation() {
        this.codeReservation = "EVT-" + ((int) (Math.random() * 90000) + 10000);
    }

    /** Définition de la date */
    @PrePersist
    public void prePersist() {
        this.dateReservation = LocalDateTime.now();

        if (this.statut == null) {
            this.statut = ReservationStatus.EN_ATTENTE;
        }

        if (this.codeReservation == null) {
            genererCodeReservation();
        }

        calculerMontantTotal();
    }
}
