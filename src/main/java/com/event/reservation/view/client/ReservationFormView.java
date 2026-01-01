package com.event.reservation.view.client;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.User;
import com.event.reservation.layout.MainLayout;
import com.event.reservation.repository.EventRepository;
import com.event.reservation.repository.ReservationRepository;
import com.event.reservation.security.SecurityService;
import com.event.reservation.service.ReservationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;

@Route(value = "event/reserve", layout = MainLayout.class)
@PageTitle("Book Event | EventApp")
@PermitAll
public class ReservationFormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final EventRepository eventRepository;
    private final ReservationService reservationService;
    private final SecurityService securityService;
    private final ReservationRepository reservationRepository;

    private Event event;
    private User currentUser;

    // UI Components
    private H2 eventTitle = new H2();
    private Span eventDate = new Span();
    private Span eventLocation = new Span();
    private Span pricePerUnit = new Span();
    private Span availabilityText = new Span();

    private IntegerField seatSpinner = new IntegerField("Number of Seats");
    private TextArea commentField = new TextArea("Comment (Optional)");
    private H3 totalPriceDisplay = new H3("0.00 MAD");

    // --- FIX 1: REMOVED global 'confirmButton' variable from here ---

    public ReservationFormView(EventRepository eventRepository,
                               ReservationService reservationService,
                               SecurityService securityService,
                               ReservationRepository reservationRepository) {
        this.eventRepository = eventRepository;
        this.reservationService = reservationService;
        this.securityService = securityService;
        this.reservationRepository = reservationRepository;

        addClassName("reservation-form-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long eventId) {
        this.currentUser = securityService.getAuthenticatedUser();

        eventRepository.findById(eventId).ifPresentOrElse(
                this::loadEventData,
                () -> {
                    Notification.show("Event not found");
                    UI.getCurrent().navigate("events");
                }
        );
    }

    private void loadEventData(Event event) {
        this.event = event;

        long placesTaken = reservationRepository.countTotalPlacesByEvent(event.getId());
        int placesLeft = event.getCapaciteMax() - (int) placesTaken;

        if (placesLeft <= 0) {
            showErrorState("This event is fully booked.");
            return;
        }

        removeAll();
        add(createFormCard(placesLeft));
        updateTotal(1);
    }

    private Component createFormCard(int placesLeft) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("booking-card");
        card.setMaxWidth("600px");
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)")
                .set("border-radius", "12px")
                .set("background", "white");

        // --- Header Section ---
        eventTitle.setText(event.getTitre());
        eventDate.setText(event.getDateDebut().format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy 'at' HH:mm")));
        eventLocation.setText(event.getLieu() + ", " + event.getVille());

        pricePerUnit.setText("Price per person: " + event.getPrixUnitaire() + " MAD");
        pricePerUnit.getStyle().set("color", "#64748b");

        availabilityText.setText(placesLeft + " seats remaining");
        availabilityText.getElement().getThemeList().add("badge success");

        VerticalLayout header = new VerticalLayout(eventTitle, eventDate, eventLocation, pricePerUnit, availabilityText);
        header.setSpacing(false);
        header.setPadding(false);

        // --- Form Section ---
        int maxBookable = Math.min(placesLeft, 10);
        seatSpinner.setMin(1);
        seatSpinner.setMax(maxBookable);
        seatSpinner.setValue(1);
        seatSpinner.setStepButtonsVisible(true);
        seatSpinner.setWidthFull();
        seatSpinner.addValueChangeListener(e -> updateTotal(e.getValue()));

        commentField.setWidthFull();
        commentField.setPlaceholder("Any special requests?");

        HorizontalLayout totalLayout = new HorizontalLayout(new Span("Total to Pay:"), totalPriceDisplay);
        totalLayout.setAlignItems(Alignment.BASELINE);
        totalLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        totalLayout.setWidthFull();
        totalLayout.getStyle().set("border-top", "1px solid #e2e8f0").set("padding-top", "1rem");

        // --- FIX 2: Create the button LOCALLY inside the method ---
        Button confirmButton = new Button("Confirm Reservation");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.setWidthFull();

        // --- FIX 3: Disable immediately and pass button to method ---
        confirmButton.addClickListener(e -> {
            confirmButton.setEnabled(false);
            submitReservation(confirmButton);
        });

        card.add(header, new Hr(), seatSpinner, commentField, totalLayout, confirmButton);
        return card;
    }

    private void updateTotal(Integer seats) {
        if (seats == null || seats < 1) {
            totalPriceDisplay.setText("0.00 MAD");
            return;
        }
        double total = seats * event.getPrixUnitaire();
        totalPriceDisplay.setText(String.format("%.2f MAD", total));
    }

    // --- FIX 4: Method now accepts the button as a parameter ---
    private void submitReservation(Button currentButton) {
        // Button is already disabled by the click listener above
        currentButton.setText("Processing...");

        try {
            int seats = seatSpinner.getValue();
            String comment = commentField.getValue();

            Reservation reservation = reservationService.createReservation(
                    event.getId(),
                    currentUser.getId(),
                    seats,
                    comment
            );

            showSuccessState(reservation);

        } catch (Exception e) {
            // --- FIX 5: Re-enable the SPECIFIC button that was clicked ---
            currentButton.setEnabled(true);
            currentButton.setText("Confirm Reservation");

            Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showSuccessState(Reservation reservation) {
        removeAll();

        VerticalLayout successCard = new VerticalLayout();
        successCard.setAlignItems(Alignment.CENTER);
        successCard.setSpacing(true);

        Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
        checkIcon.setColor("green");
        checkIcon.setSize("64px");

        H2 title = new H2("Booking Confirmed!");
        Span msg = new Span("You have successfully booked " + reservation.getNombrePlaces() + " seats.");

        H3 codeDisplay = new H3(reservation.getCodeReservation());
        codeDisplay.getStyle().set("background", "#f1f5f9")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("letter-spacing", "2px")
                .set("color", "#334155");

        Button myReservationsBtn = new Button("Go to My Reservations",
                e -> UI.getCurrent().navigate("my-reservations"));
        myReservationsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        successCard.add(checkIcon, title, msg, codeDisplay, myReservationsBtn);
        add(successCard);
    }

    private void showErrorState(String message) {
        removeAll();
        add(new H2("Unavailable"), new Span(message),
                new Button("Back", e -> UI.getCurrent().navigate("events")));
    }
}