package com.event.reservation.repository;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.User;
import com.event.reservation.entity.EventCategory;
import com.event.reservation.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Trouver par catégorie
    List<Event> findByCategorie(EventCategory categorie);

    // --- FIX ADDED HERE ---
    List<Event> findByStatut(EventStatus statut);
    // ----------------------

    // Événements publiés entre deux dates
    List<Event> findByStatutAndDateDebutBetween(EventStatus statut, LocalDateTime start, LocalDateTime end);

    // Événements d’un organisateur + statut
    List<Event> findByOrganisateurAndStatut(User organisateur, EventStatus statut);

    // Événements disponibles (publiés et non terminés)
    List<Event> findByStatutIn(List<EventStatus> statuts);

    long countByStatut(EventStatus statut);

    long countByCategorie(EventCategory categorie);

    // Trouver par lieu ou ville
    List<Event> findByLieuContainingIgnoreCaseOrVilleContainingIgnoreCase(String lieu, String ville);

    // Recherche par titre (mot clé)
    List<Event> findByTitreContainingIgnoreCase(String keyword);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.organisateur.id = :organisateurId")
    long countByOrganisateurId(@Param("organisateurId") Long organisateurId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.organisateur.id = :organisateurId AND e.statut = :statut")
    long countByOrganisateurIdAndStatut(@Param("organisateurId") Long organisateurId, @Param("statut") EventStatus statut);

    List<Event> findTop5ByOrganisateurIdOrderByDateCreationDesc(Long organisateurId);

    List<Event> findByPrixUnitaireBetween(double min, double max);

    List<Event> findEventsByOrganisateurId(Long organisateurId);

    @Query("SELECT e FROM Event e ORDER BY (SELECT COUNT(r) FROM Reservation r WHERE r.evenement = e) DESC")
    List<Event> findPopularEvents();

    @Query("""
       SELECT e FROM Event e
       WHERE
            (:keyword IS NULL OR LOWER(e.titre) LIKE LOWER(CONCAT('%', :keyword, '%')))
       AND  (:categorie IS NULL OR e.categorie = :categorie)
       AND  (:ville IS NULL OR LOWER(e.ville) LIKE LOWER(CONCAT('%', :ville, '%')))
       AND  (CAST(:startDate AS timestamp) IS NULL OR e.dateDebut >= :startDate)
       AND  (CAST(:endDate AS timestamp) IS NULL OR e.dateDebut <= :endDate)
       AND  (:minPrice IS NULL OR e.prixUnitaire >= :minPrice)
       AND  (:maxPrice IS NULL OR e.prixUnitaire <= :maxPrice)
       """)
    List<Event> searchEvents(@Param("categorie") EventCategory categorie,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("ville") String ville,
                             @Param("minPrice") Double minPrice,
                             @Param("maxPrice") Double maxPrice,
                             @Param("keyword") String keyword);
}