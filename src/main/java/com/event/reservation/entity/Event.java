package com.event.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 100, message = "Le titre doit contenir entre 5 et 100 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La catégorie est obligatoire")
    private EventCategory categorie;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    private Integer capaciteMax;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private Double prixUnitaire;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "organisateur_id")
    private User organisateur;

    @Enumerated(EnumType.STRING)
    private EventStatus statut;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public boolean isValidForPublication() {
        return titre != null &&
                titre.length() >= 5 &&
                categorie != null &&
                dateDebut != null &&
                dateFin != null &&
                dateFin.isAfter(dateDebut) &&
                prixUnitaire >= 0 &&
                capaciteMax > 0 &&
                lieu != null &&
                ville != null;
    }
}
