package com.event.reservation.view.organizer;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.entity.User;
import com.event.reservation.view.organizer.OrganizerLayout;
import com.event.reservation.security.SecurityService;
import com.event.reservation.service.EventService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "organizer/dashboard", layout = OrganizerLayout.class)
@PageTitle("Dashboard | Organizer")
@RolesAllowed("ORGANIZER")
@CssImport(value = "./styles/organizer-style.css")

public class OrganizerDashboardView extends VerticalLayout {

    private final EventService eventService;
    private final SecurityService securityService;

    public OrganizerDashboardView(EventService eventService, SecurityService securityService) {
        this.eventService = eventService;
        this.securityService = securityService;

        addClassName("organizer-dashboard");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        User currentUser = securityService.getAuthenticatedUser();
        // Fallback for safety if user is null (shouldn't happen with @RolesAllowed)
        if (currentUser == null) return;

        // 1. Header
        add(createHeader(currentUser));

        // 2. Stats Cards (Overview)
        add(createStatsSection(currentUser.getId()));

        // 3. Main Content Split (Breakdown + Recent Events)
        Div mainContent = new Div();
        mainContent.addClassName("dashboard-main-content");

        // Left: Status Breakdown
        Component statusComponent = createStatusBreakdown(currentUser.getId());

        // Right: Recent Events Grid
        Component recentEventsComponent = createRecentEvents(currentUser.getId());

        mainContent.add(statusComponent, recentEventsComponent);
        add(mainContent);
    }

    private Component createHeader(User user) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout welcome = new VerticalLayout();
        welcome.setSpacing(false);
        welcome.setPadding(false);
        welcome.add(new H2("Welcome back, " + user.getNom()));
        welcome.add(new Span("Here's what's happening with your events today."));

        Button createBtn = new Button("Create New Event", new Icon(VaadinIcon.PLUS));
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createBtn.addClickListener(e -> UI.getCurrent().navigate("organizer/events/new"));

        header.add(welcome, createBtn);
        return header;
    }

    private Component createStatsSection(Long orgId) {
        Div statsContainer = new Div();
        statsContainer.addClassName("stats-container");

        long totalEvents = eventService.countEventsByOrganizer(orgId);
        long totalReservations = eventService.countOrganizerReservations(orgId);
        Double totalRevenue = eventService.calculateOrganizerRevenue(orgId);

        statsContainer.add(
                createStatCard("Total Events", String.valueOf(totalEvents), VaadinIcon.CALENDAR, "blue"),
                createStatCard("Total Reservations", String.valueOf(totalReservations), VaadinIcon.TICKET, "purple"),
                createStatCard("Revenue Generated", String.format("%.2f MAD", totalRevenue), VaadinIcon.WALLET, "green")
        );

        return statsContainer;
    }

    private Div createStatCard(String title, String value, VaadinIcon icon, String colorTheme) {
        Div card = new Div();
        card.addClassName("stat-card");
        card.addClassName("stat-card-" + colorTheme);

        Icon i = icon.create();
        i.addClassName("stat-icon");

        Div info = new Div();
        Span titleSpan = new Span(title);
        titleSpan.addClassName("stat-title");
        Span valueSpan = new Span(value);
        valueSpan.addClassName("stat-value");

        info.add(titleSpan, valueSpan);
        card.add(i, info);
        return card;
    }

    private Component createStatusBreakdown(Long orgId) {
        Div container = new Div();
        container.addClassName("dashboard-section");

        H3 title = new H3("Event Status Overview");
        container.add(title);

        long total = eventService.countEventsByOrganizer(orgId);
        long published = eventService.countEventsByOrganizerAndStatus(orgId, EventStatus.PUBLIE);
        long drafts = eventService.countEventsByOrganizerAndStatus(orgId, EventStatus.BROUILLON);
        long cancelled = eventService.countEventsByOrganizerAndStatus(orgId, EventStatus.ANNULE);

        container.add(createProgressBar("Published", published, total, "success"));
        container.add(createProgressBar("Drafts", drafts, total, "contrast"));
        container.add(createProgressBar("Cancelled", cancelled, total, "error"));

        return container;
    }

    private Component createProgressBar(String label, long count, long total, String theme) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.getStyle().set("margin-bottom", "15px");

        HorizontalLayout labelRow = new HorizontalLayout();
        labelRow.setWidthFull();
        labelRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        labelRow.add(new Span(label), new Span(count + " Events"));

        ProgressBar progressBar = new ProgressBar();
        double value = total > 0 ? (double) count / total : 0;
        progressBar.setValue(value);
        progressBar.addThemeNames(theme);

        layout.add(labelRow, progressBar);
        return layout;
    }

    private Component createRecentEvents(Long orgId) {
        Div container = new Div();
        container.addClassName("dashboard-section");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.add(new H3("Recent Events"));

        Button viewAll = new Button("View All");
        viewAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewAll.addClickListener(e -> UI.getCurrent().navigate("organizer/events")); // Assuming this route exists
        header.add(viewAll);

        Grid<Event> grid = new Grid<>(Event.class, false);
        grid.addClassName("recent-grid");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.addColumn(Event::getTitre).setHeader("Title").setAutoWidth(true);
        grid.addColumn(event -> event.getDateDebut().toLocalDate()).setHeader("Date");
        grid.addComponentColumn(event -> {
            Span badge = new Span(event.getStatut().name());
            badge.getElement().getThemeList().add("badge " + getStatusTheme(event.getStatut()));
            return badge;
        }).setHeader("Status");

        List<Event> recentEvents = eventService.getRecentEventsForOrganizer(orgId);
        grid.setItems(recentEvents);

        container.add(header, grid);
        return container;
    }

    private String getStatusTheme(EventStatus status) {
        switch (status) {
            case PUBLIE: return "success";
            case BROUILLON: return "contrast";
            case ANNULE: return "error";
            default: return "primary";
        }
    }
}