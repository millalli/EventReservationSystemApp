package com.event.reservation.view.event;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.entity.User;
import com.event.reservation.layout.MainLayout;
import com.event.reservation.service.EventService;
import com.event.reservation.view.client.ReservationFormView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll; // Changed from AnonymousAllowed
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route(value = "events/:eventId", layout = MainLayout.class)
@PageTitle("Event Details")
@PermitAll // <--- Only logged-in users can view this page now
@CssImport(value = "./styles/event-detail-style.css")

public class EventDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final EventService eventService;
    // SecurityService removed: We know the user is logged in if they reach this page.

    private Event event;
    private Long eventId;

    // UI Components
    private Image heroImage;
    private H1 titleField;
    private Span dateBadge;
    private Span categoryBadge;
    private Paragraph descriptionField;
    private Span locationField;
    private Span priceField;
    private Span spotsField;
    private ProgressBar spotsProgressBar;
    private Button bookButton;
    private Div organizerCard;
    private IFrame googleMapFrame;

    @Autowired
    public EventDetailView(EventService eventService) {
        this.eventService = eventService;

        addClassName("event-detail-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String eventIdParam = beforeEnterEvent.getRouteParameters().get("eventId").orElse(null);
        if (eventIdParam != null) {
            try {
                this.eventId = Long.parseLong(eventIdParam);
                this.event = eventService.getEventById(eventId);
                constructUI();
            } catch (RuntimeException e) {
                beforeEnterEvent.rerouteTo(EventsView.class);
            }
        } else {
            beforeEnterEvent.rerouteTo(EventsView.class);
        }
    }

    private void constructUI() {
        removeAll();

        // 1. Navigation Breadcrumb
        Button backButton = new Button("Back to Events", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClassName("back-button");
        backButton.addClickListener(e -> UI.getCurrent().navigate(EventsView.class));

        Div container = new Div();
        container.addClassName("detail-container");

        // 2. Main Layout
        Div contentLayout = new Div();
        contentLayout.addClassName("detail-content-layout");

        // Left & Right Columns
        Div mainInfoDiv = createMainInfoSection();
        Div bookingCard = createBookingCard();

        contentLayout.add(mainInfoDiv, bookingCard);
        container.add(backButton, contentLayout);
        add(container);
    }

    private Div createMainInfoSection() {
        Div mainInfo = new Div();
        mainInfo.addClassName("detail-main-info");

        // Hero Image
        String imageUrl = (event.getImageUrl() != null && !event.getImageUrl().isEmpty())
                ? event.getImageUrl()
                : "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=1000&q=80";
        heroImage = new Image(imageUrl, "Event Cover");
        heroImage.addClassName("detail-hero-image");

        // Meta Header
        HorizontalLayout metaHeader = new HorizontalLayout();
        metaHeader.addClassName("detail-meta-header");

        categoryBadge = new Span(event.getCategorie().name());
        categoryBadge.addClassName("detail-category-badge");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy 'at' HH:mm", Locale.ENGLISH);
        dateBadge = new Span(event.getDateDebut().format(formatter));
        dateBadge.addClassName("detail-date-badge");

        metaHeader.add(categoryBadge, dateBadge);

        // Title & Description
        titleField = new H1(event.getTitre());
        titleField.addClassName("detail-title");

        H3 descTitle = new H3("About this event");
        descriptionField = new Paragraph(event.getDescription());
        descriptionField.addClassName("detail-description");

        // Location
        H3 locTitle = new H3("Location");
        locationField = new Span(event.getLieu() + ", " + event.getVille());
        locationField.addClassName("detail-location-text");
        Icon locIcon = VaadinIcon.MAP_MARKER.create();
        HorizontalLayout locLine = new HorizontalLayout(locIcon, locationField);
        locLine.setAlignItems(FlexComponent.Alignment.CENTER);

        // Map
        String mapUrl = "https://maps.google.com/maps?q=" + event.getVille() + "+" + event.getLieu() + "&t=&z=13&ie=UTF8&iwloc=&output=embed";
        googleMapFrame = new IFrame(mapUrl);
        googleMapFrame.addClassName("detail-map-frame");
        googleMapFrame.setHeight("300px");
        googleMapFrame.setWidthFull();

        // Organizer
        H3 orgTitle = new H3("Hosted by");
        organizerCard = createOrganizerInfo(event.getOrganisateur());

        mainInfo.add(heroImage, metaHeader, titleField, descTitle, descriptionField, new Hr(), locTitle, locLine, googleMapFrame, new Hr(), orgTitle, organizerCard);
        return mainInfo;
    }

    private Div createBookingCard() {
        Div card = new Div();
        card.addClassName("booking-card");

        // Price
        Div priceSection = new Div();
        Span priceLabel = new Span("Total Price");
        priceLabel.addClassName("booking-price-label");

        priceField = new Span(String.format("%.2f MAD", event.getPrixUnitaire()));
        priceField.addClassName("booking-price-value");
        priceSection.add(priceLabel, priceField);

        // Availability
        int available = eventService.countAvailablePlaces(event.getId());
        int total = event.getCapaciteMax();
        double percentage = (double) available / total;

        spotsField = new Span(available + " spots remaining");
        spotsField.addClassName("booking-spots-text");

        spotsProgressBar = new ProgressBar();
        spotsProgressBar.setValue(1 - percentage);
        spotsProgressBar.addClassName("booking-progress");

        // Button
        bookButton = new Button("Reserve a Spot");
        bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        bookButton.addClassName("booking-btn-main");
        bookButton.setWidthFull();
        bookButton.addClickListener(e -> {
            // Navigate to ReservationFormView with the Event ID
            UI.getCurrent().navigate(ReservationFormView.class, event.getId());
        });
        if (event.getStatut() != EventStatus.PUBLIE || available <= 0) {
            bookButton.setEnabled(false);
            bookButton.setText(available <= 0 ? "Sold Out" : "Unavailable");
        } else {
            // No manual security check needed here
            bookButton.addClickListener(e -> handleBooking());
        }

        Paragraph refundPolicy = new Paragraph("Free cancellation up to 48 hours before the event.");
        refundPolicy.addClassName("booking-policy-text");

        card.add(priceSection, new Hr(), spotsField, spotsProgressBar, bookButton, refundPolicy);
        return card;
    }

    private Div createOrganizerInfo(User organizer) {
        Div container = new Div();
        container.addClassName("organizer-container");

        Icon avatar = VaadinIcon.USER.create();
        avatar.addClassName("organizer-avatar");

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        Span name = new Span(organizer.getNom() + " " + organizer.getPrenom());
        name.addClassName("organizer-name");

        Span role = new Span("Verified Organizer");
        role.addClassName("organizer-role");

        info.add(name, role);

        HorizontalLayout layout = new HorizontalLayout(avatar, info);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        container.add(layout);

        return container;
    }

    private void handleBooking() {
        // OLD: UI.getCurrent().navigate("reservation/" + event.getId());

        // NEW: Match the @Route(value = "event/reserve")
        UI.getCurrent().navigate("event/reserve/" + event.getId());
    }
}