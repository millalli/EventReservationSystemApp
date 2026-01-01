package com.event.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime dateInscription;

    private Boolean actif = true;

    private String telephone;
    public boolean isActif() {

        return Boolean.TRUE.equals(this.actif);

    }

    public boolean isEnabled() {
        return this.actif;
    }
    // Constructeur pratique
    public User(String nom, String prenom, String email, String password, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dateInscription = LocalDateTime.now();
        this.actif = true;
    }
}
