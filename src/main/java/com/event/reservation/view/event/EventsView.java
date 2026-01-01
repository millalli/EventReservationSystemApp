package com.event.reservation.view.event;

import com.event.reservation.layout.MainLayout;
import com.event.reservation.entity.Event;
import com.event.reservation.entity.EventCategory;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.service.EventService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "events", layout = MainLayout.class)
@PageTitle("Event Gallery | Discover")
@CssImport(value = "./styles/events-style.css")
@AnonymousAllowed
public class EventsView extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(EventsView.class);
    private final EventService eventService;

    // Filters
    private TextField searchField;
    private ComboBox<EventCategory> categoryFilter;
    private DatePicker startDateFilter;
    private TextField cityFilter;
    private NumberField minPriceFilter;
    private ComboBox<String> sortByComboBox;

    // Container for cards
    private Div eventsContainer;

    public EventsView(EventService eventService) {
        this.eventService = eventService;

        addClassName("events-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        // 1. Hero Section (Title + Search)
        add(createHeroSection());

        // 2. Filter Bar (White strip)
        add(createFilterBar());

        // 3. Events Grid Container
        eventsContainer = new Div();
        eventsContainer.addClassName("events-grid-layout");
        add(eventsContainer);

        // 4. Footer
        add(createFooter());

        // Initial Load
        refreshEvents();
    }

    private Div createHeroSection() {
        Div hero = new Div();
        hero.addClassName("events-hero-section");

        Div overlay = new Div();
        overlay.addClassName("events-hero-overlay");

        VerticalLayout content = new VerticalLayout();
        content.addClassName("events-hero-content");
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        H1 title = new H1("Discover Your Next Experience");
        title.addClassName("events-hero-title");

        Span subtitle = new Span("Music, Art, Sport, and everything in between.");
        subtitle.addClassName("events-hero-subtitle");

        // Main Search Bar
        searchField = new TextField();
        searchField.setPlaceholder("Search for an event, artist, or venue...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("main-search-field");
        searchField.setClearButtonVisible(true);
        searchField.addKeyPressListener(Key.ENTER, e -> refreshEvents());

        content.add(title, subtitle, searchField);
        overlay.add(content);
        hero.add(overlay);

        return hero;
    }

    private Div createFilterBar() {
        Div filterContainer = new Div();
        filterContainer.addClassName("filter-bar-container");

        // Filter Components
        categoryFilter = new ComboBox<>();
        categoryFilter.setItems(EventCategory.values());
        categoryFilter.setItemLabelGenerator(this::formatCategory);
        categoryFilter.setPlaceholder("Category");
        categoryFilter.setClearButtonVisible(true);

        startDateFilter = new DatePicker();
        startDateFilter.setPlaceholder("Date");

        cityFilter = new TextField();
        cityFilter.setPlaceholder("City");
        cityFilter.setPrefixComponent(new Icon(VaadinIcon.MAP_MARKER));

        minPriceFilter = new NumberField();
        minPriceFilter.setPlaceholder("Min Price");

        sortByComboBox = new ComboBox<>();
        sortByComboBox.setItems("Date (Asc)", "Date (Desc)", "Price (Low-High)");
        sortByComboBox.setPlaceholder("Sort by");
        sortByComboBox.setValue("Date (Asc)");

        // --- Apply Button ---
        Button filterBtn = new Button("Apply", new Icon(VaadinIcon.FILTER));
        filterBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        filterBtn.addClickListener(e -> refreshEvents());
        filterBtn.addClassName("filter-btn");

        // --- NEW: Reset Button ---
        Button resetBtn = new Button("Reset", new Icon(VaadinIcon.REFRESH));
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY); // Tertiary looks cleaner next to Primary
        resetBtn.addClassName("filter-btn");
        resetBtn.addClickListener(e -> {
            // Clear all fields
            searchField.clear();
            categoryFilter.clear();
            startDateFilter.clear();
            cityFilter.clear();
            minPriceFilter.clear();
            sortByComboBox.setValue("Date (Asc)");

            // Reload original list
            refreshEvents();
        });

        // Layout
        Div layout = new Div();
        layout.addClassName("filters-layout");

        // Added resetBtn to the layout
        layout.add(categoryFilter, startDateFilter, cityFilter, minPriceFilter, sortByComboBox, filterBtn, resetBtn);

        filterContainer.add(layout);
        return filterContainer;
    }

    private void refreshEvents() {
        eventsContainer.removeAll();

        // Retrieve values
        EventCategory category = categoryFilter.getValue();
        LocalDateTime startDate = startDateFilter.getValue() != null ? startDateFilter.getValue().atStartOfDay() : null;
        String city = cityFilter.getValue();
        Double min = minPriceFilter.getValue();
        String keyword = searchField.getValue();

        // Service Call
        List<Event> events = eventService.searchEvents(category, startDate, null, city, min, null, keyword);

        if (events.isEmpty()) {
            showEmptyState("No events found", "Try adjusting your filters.");
            return;
        }

        boolean foundAny = false;

        for (Event event : events) {
            // --- FIX 1: Filter Logic ---
            // Only show PUBLISHED events
            // AND ensure the event hasn't happened yet (Future Only)
            if (event.getStatut() == EventStatus.PUBLIE
                    && event.getDateDebut().isAfter(LocalDateTime.now())) {

                eventsContainer.add(createEventCard(event));
                foundAny = true;
            }
        }

        // If we found events in DB, but filtered them all out because they were in the past:
        if (!foundAny) {
            showEmptyState("No upcoming events", "Check back later for new events.");
        }
    }

    private void showEmptyState(String title, String subtitle) {
        Div emptyState = new Div();
        emptyState.addClassName("empty-state");
        emptyState.add(new H3(title), new Paragraph(subtitle));
        eventsContainer.add(emptyState);
    }

    private Div createEventCard(Event event) {
        Div card = new Div();
        card.addClassName("event-card");

        // 1. Image
        String imageUrl = event.getImageUrl() != null && !event.getImageUrl().isEmpty()
                ? event.getImageUrl()
                : getDefaultImage(event.getCategorie());

        Image image = new Image(imageUrl, "Event Image");
        image.addClassName("card-image");

        // 2. Content
        Div content = new Div();
        content.addClassName("card-content");

        // Badges
        Span catBadge = new Span(formatCategory(event.getCategorie()));
        catBadge.addClassName("badge-category");

        H3 title = new H3(event.getTitre());
        title.addClassName("card-title");

        // Date Row
        HorizontalLayout dateRow = new HorizontalLayout(new Icon(VaadinIcon.CALENDAR), new Span(formatDateTime(event.getDateDebut())));
        dateRow.addClassName("card-meta-row");

        // Location Row
        HorizontalLayout locRow = new HorizontalLayout(new Icon(VaadinIcon.MAP_MARKER), new Span(event.getVille()));
        locRow.addClassName("card-meta-row");

        // Footer Row (Price & Button)
        Div footerInfo = new Div();
        footerInfo.addClassName("card-footer-info");

        Span price = new Span(String.format("%.0f MAD", event.getPrixUnitaire()));
        price.addClassName("card-price");

        Button detailsBtn = new Button("View Details");
        detailsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        detailsBtn.addClassName("card-button");
        detailsBtn.addClickListener(e -> UI.getCurrent().navigate("events/" + event.getId()));

        footerInfo.add(price, detailsBtn);

        content.add(catBadge, title, dateRow, locRow, footerInfo);
        card.add(image, content);

        // Make whole card clickable
        card.addClickListener(e -> UI.getCurrent().navigate("events/" + event.getId()));

        return card;
    }

    private Div createFooter() {
        Div footer = new Div();
        footer.addClassName("events-footer");
        Span copyright = new Span("© 2024 Event Gallery. All rights reserved.");
        HorizontalLayout links = new HorizontalLayout();
        links.addClassName("footer-links");
        links.add(new Anchor("#", "Privacy Policy"), new Anchor("#", "Terms of Service"), new Anchor("#", "Support"));
        VerticalLayout footerContent = new VerticalLayout(copyright, links);
        footerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.add(footerContent);
        return footer;
    }

    // Helpers
    private String formatCategory(EventCategory category) {
        return category != null ? category.name().substring(0, 1).toUpperCase() + category.name().substring(1).toLowerCase() : "All";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")) : "";
    }

    private String getDefaultImage(EventCategory cat) {
        return "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?auto=format&fit=crop&w=500&q=60";
    }
}