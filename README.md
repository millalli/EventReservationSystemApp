# Système de Gestion de Réservations d'Événements (Event Reservation System | Event Gallery)

Application web complète développée avec **Spring Boot** et **Vaadin** permettant la gestion et la réservation d'événements culturels (concerts, théâtre, conférences, etc.).

## Contexte du Projet

Ce projet a été réalisé dans le cadre du module Java Avancé. L'objectif est de fournir une plateforme centralisée où :

- Les **Organisateurs** créent et gèrent leurs événements.
- Les **Clients** consultent et réservent des places.
- Les **Administrateurs** supervisent l'ensemble de la plateforme.

##  Technologies Utilisées

**Backend :** Java 17+, Spring Boot 3.x
**Frontend :** Vaadin 24.x (Framework UI 100% Java)
**Base de données :** H2 Database (Mode Embedded)
**Sécurité :** Spring Security (BCrypt, Session-based)

- **Build :** Maven

##  Installation et Lancement

1.  **Cloner le projet :**

En raison de problèmes de connexion (timeout) rencontrés lors du déploiement sur GitHub, la commande `git clone` pourrait ne pas fonctionner correctement pour récupérer l'intégralité du projet.

Pour pallier ce problème, **une vidéo de démonstration complète** montrant l'exécution et les fonctionnalités de l'application est fournie

###  Vidéo d'exécution

2.  **Lancer l'application :**

    - Ouvrez le projet dans IntelliJ IDEA (ou Eclipse).
    - Exécutez la classe principale : `EventReservationSystemApplication.java`.
    - Ou via le terminal : `mvn spring-boot:run`.

3.  **Accéder à l'application :**
    - Ouvrez votre navigateur sur : `http://localhost:8080`

##  Identifiants de Test (Données Initiales)

Conformément au cahier des charges, les utilisateurs suivants sont pré-configurés:

| Rôle             | Email                  | Mot de passe  |
| :--------------- | :--------------------- | :------------ |
| **Admin**        | `admin@gmail.com`      | `admin12345`  |
| **Organisateur** | `organizer1@gmail.com` | `org12345`    |
| **Client**       | `client1@gmail.com`    | `client12345` |

## 💾 Base de Données & Données de Test

Le script SQL permettant d'injecter les données initiales (utilisateurs par défaut, événements exemples) se trouve dans les ressources du projet :

**Chemin du fichier :** `src/main/resources/insert_data.sql`

##  Structure du Projet

Le code source est organisé selon une architecture modulaire claire dans le package `com.event.reservation` :

- **config/** : Configuration de la sécurité (`SecurityConfig`).
- **controller/** : Gestion des endpoints REST d'authentification (`AuthController`).
- **dto/** : Objets de transfert de données (`LoginRequest`, `RegisterRequest`, `AuthResponse`).
- **entity/** : Modèles de données JPA (`User`, `Event`, `Reservation`, `Role`, `EventCategory`, `EventStatus`, `ReservationStatus`).
- **exception/** : Gestion centralisée des erreurs (`ResourceNotFoundException`, `BadRequestException`, `ConflictException`).
- **layout/** : Structure principale de l'interface utilisateur (`MainLayout`).
- **repository/** : Interfaces d'accès aux données (`UserRepository`, `EventRepository`, `ReservationRepository`).
- **security/** : Implémentation de la sécurité Spring (`UserDetailsServiceImpl`, `SecurityService`, `UserDetailsImpl`).
- **service/** : Logique métier (Interfaces et Implémentations dans `service/impl`).
- **view/** : Vues Vaadin organisées par rôle :
  - `home/` : Page d'accueil publique.
  - `login/` & `register/` : Pages d'authentification.
  - `admin/` : Tableaux de bord administrateur (`UserManagement`, `AllEvents`).
  - `organizer/` : Gestion des événements organisateur (`MyEvents`, `EventForm`).
  - `client/` : Réservations client (`MyReservations`, `ReservationForm`).
  - `event/` : Détails et listes d'événements.

##  Fonctionnalités Principales

- **Authentification & Inscription** : Sécurisée avec gestion des rôles (Admin, Organizer, Client).
- **Gestion des Événements** : CRUD complet, upload d'images, catégorisation.
- **Réservations** : Processus de réservation en temps réel avec calcul de prix et gestion de capacité.
- **Tableaux de Bord** : Vues statistiques dédiées pour chaque type d'utilisateur.

##  Auteur

Projet réalisé par [Hafsa Elhouaoui] - Année 2025/2026.

## Architecture du Projet

```mermaid
graph TD
    subgraph Presentation_Layer_Vaadin [Presentation Layer (Package: view)]
        direction TB
        Admin[<b>Admin Views</b><br/>AdminDashboard, UserManagement<br/>AllEventsManagement]
        Organizer[<b>Organizer Views</b><br/>OrganizerDashboard, MyEvents<br/>EventForm, EventReservations]
        Client[<b>Client Views</b><br/>Dashboard, MyReservations<br/>ReservationForm]
        Public[<b>Public/Shared Views</b><br/>Home, Login, Register<br/>EventDetail, EventsView]
    end

    subgraph Security_Layer [Security & Controller]
        SecConfig[SecurityConfig]
        AuthCtrl[AuthController]
        SecServ[SecurityService / UserDetailsServiceImpl]
    end

    subgraph Business_Layer [Service Layer (Package: service)]
        direction TB
        AuthS[AuthService]
        UserS[UserService]
        EventS[EventService]
        ResS[ReservationService]
        DashS[DashboardService]
    end

    subgraph Data_Access_Layer [Repository Layer (Package: repository)]
        UserR[UserRepository]
        EventR[EventRepository]
        ResR[ReservationRepository]
    end

    subgraph Domain_Layer [Database & Entities]
        DB[(Database H2/MySQL)]
        Entities[<b>Entities</b><br/>User, Role<br/>Event, EventCategory<br/>Reservation, ReservationStatus]
    end

    %% Relations
    Presentation_Layer_Vaadin --> SecConfig
    Public --> AuthCtrl
    
    %% Views call Services
    Admin --> UserS
    Admin --> EventS
    Admin --> ResS
    Organizer --> EventS
    Organizer --> ResS
    Client --> ResS
    Client --> EventS
    
    %% Controller/Security calls Services
    AuthCtrl --> AuthS
    SecServ --> UserR

    %% Services call Repositories
    AuthS --> UserR
    UserS --> UserR
    EventS --> EventR
    ResS --> ResR
    DashS --> EventR & ResR

    %% Repositories access DB
    UserR --> DB
    EventR --> DB
    ResR --> DB
    
    %% Entities Mapping
    Data_Access_Layer -.-> Entities
    end
```
