package com.event.reservation.service.impl;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.User;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.entity.EventCategory;
import com.event.reservation.exception.BadRequestException;
import com.event.reservation.exception.ResourceNotFoundException;
import com.event.reservation.repository.EventRepository;
import com.event.reservation.repository.ReservationRepository;
import com.event.reservation.service.EventService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'id : " + id));
    }

    @Override
    public Event createEvent(Event event, User creator) {
        if (creator == null) {
            throw new BadRequestException("Utilisateur non trouvé");
        }

        if (creator.getRole().isClient()) {
            throw new BadRequestException("Seuls ADMIN ou ORGANIZER peuvent créer un événement");
        }

        event.setOrganisateur(creator);
        event.setStatut(EventStatus.BROUILLON);
        event.setDateCreation(LocalDateTime.now());
        event.setDateModification(LocalDateTime.now());

        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event eventUpdates, User userEditing) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable"));

        if (existing.getStatut() == EventStatus.TERMINE) {
            throw new BadRequestException("Un événement terminé ne peut pas être modifié");
        }

        if (!existing.getOrganisateur().equals(userEditing) && !userEditing.getRole().isAdmin()) {
            throw new BadRequestException("Vous ne pouvez modifier que vos propres événements");
        }

        existing.setTitre(eventUpdates.getTitre());
        existing.setDescription(eventUpdates.getDescription());
        existing.setCategorie(eventUpdates.getCategorie());
        existing.setDateDebut(eventUpdates.getDateDebut());
        existing.setDateFin(eventUpdates.getDateFin());
        existing.setLieu(eventUpdates.getLieu());
        existing.setVille(eventUpdates.getVille());
        existing.setCapaciteMax(eventUpdates.getCapaciteMax());
        existing.setPrixUnitaire(eventUpdates.getPrixUnitaire());
        existing.setImageUrl(eventUpdates.getImageUrl());
        existing.setDateModification(LocalDateTime.now());

        return eventRepository.save(existing);
    }

    @Override
    public List<Event> findAllEventsByOrganizer(Long organizerId) {
        return eventRepository.findEventsByOrganisateurId(organizerId);
    }

    @Override
    public void publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        // Validation logic can go here (e.g., check if date is in future)
        event.setStatut(EventStatus.PUBLIE);
        eventRepository.save(event);
    }

    @Override
    public void cancelEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        event.setStatut(EventStatus.ANNULE);
        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
        // You might want to check for existing reservations before deleting
        eventRepository.deleteById(eventId);
    }





    @Override
    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findEventsByOrganisateurId(organizerId);
    }


    @Override
    public long countEventsByOrganizer(Long orgId) {
        return eventRepository.countByOrganisateurId(orgId);
    }

    @Override
    public long countEventsByOrganizerAndStatus(Long orgId, EventStatus status) {
        return eventRepository.countByOrganisateurIdAndStatut(orgId, status);
    }

    @Override
    public List<Event> getRecentEventsForOrganizer(Long orgId) {
        return eventRepository.findTop5ByOrganisateurIdOrderByDateCreationDesc(orgId);
    }

    @Override
    public long countOrganizerReservations(Long orgId) {
        return reservationRepository.countTotalReservationsForOrganizer(orgId);
    }

    @Override
    public Double calculateOrganizerRevenue(Long orgId) {
        return reservationRepository.calculateTotalRevenueForOrganizer(orgId);
    }
    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    //Admin part :
    @Override
    public List<Event> searchEvents(EventCategory category,
                                    LocalDateTime startDate,
                                    LocalDateTime endDate,
                                    String city,
                                    Double minPrice,
                                    Double maxPrice,
                                    String keyword) {
        return eventRepository.searchEvents(category, startDate, endDate, city, minPrice, maxPrice, keyword);
    }

    // --- Implement the missing helper methods from your interface ---

    @Override
    public int countAvailablePlaces(Long eventId) {
        Event event = getEventById(eventId);

        // ERROR WAS HERE: Cast (int) is required because repository returns long
        int reserved = (int) reservationRepository.countByEvenementIdAndStatutNot(eventId, com.event.reservation.entity.ReservationStatus.ANNULEE);

        return event.getCapaciteMax() - reserved;
    }

    @Override
    public List<Event> getPopularEvents() {
        // Simple implementation: just return all, or limit to top 5
        // You would ideally want a custom query for this later
        return eventRepository.findAll().stream().limit(6).toList();
    }
    // --- FIX 1: Implement findAllEvents ---
    @Override
    public List<Event> findAllEvents(String filter) {
        // Reuse the advanced search logic you already built!
        // passing 'null' for everything except the keyword (filter)
        return searchEvents(null, null, null, null, null, null, filter);
    }

    // --- FIX 2: Implement updateEventStatus ---
    @Override
    public void updateEventStatus(Long id, EventStatus status) {
        Event event = getEventById(id); // Reuses your existing helper
        event.setStatut(status);
        eventRepository.save(event);
    }
    @Override
    public void checkTerminatedEvents() {
        // Logic to auto-close events past their date
        List<Event> activeEvents = eventRepository.findByStatut(EventStatus.PUBLIE);
        LocalDateTime now = LocalDateTime.now();
        for (Event event : activeEvents) {
            if (event.getDateFin() != null && event.getDateFin().isBefore(now)) {
                event.setStatut(EventStatus.TERMINE);
                eventRepository.save(event);
            }
        }
    }
}
