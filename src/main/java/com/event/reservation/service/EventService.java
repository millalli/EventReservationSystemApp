package com.event.reservation.service;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.User;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.entity.EventCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event save(Event event);
    Optional<Event> findById(Long id);
    Event getEventById(Long id);

    Event createEvent(Event event, User creator);

    Event updateEvent(Long id, Event eventUpdates, User userEditing);
    List<Event> findAllEventsByOrganizer(Long organizerId);
    void publishEvent(Long id);

    void cancelEvent(Long id);

    void deleteEvent(Long id);
    List<Event> findAllEvents(String filter);

    void updateEventStatus(Long id, EventStatus status);
    List<Event> searchEvents(
            EventCategory category,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String city,
            Double minPrice,
            Double maxPrice,
            String keyword
    );

    int countAvailablePlaces(Long eventId);

    List<Event> getPopularEvents(); // les plus réservés

    List<Event> getEventsByOrganizer(Long organizerId);

    void checkTerminatedEvents();
    // New Organizer Dashboard Methods
    long countEventsByOrganizer(Long orgId);
    long countEventsByOrganizerAndStatus(Long orgId, EventStatus status);
    List<Event> getRecentEventsForOrganizer(Long orgId);

    // Revenue & Reservations
    long countOrganizerReservations(Long orgId);
    Double calculateOrganizerRevenue(Long orgId);
}
