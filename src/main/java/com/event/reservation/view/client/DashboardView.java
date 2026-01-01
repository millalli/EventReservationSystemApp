package com.event.reservation.view.client;

import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.User;
import com.event.reservation.layout.MainLayout;
import com.event.reservation.service.ReservationService;
import com.event.reservation.security.SecurityService; // Ensure you have this from previous steps
import com.event.reservation.view.event.EventsView;
import com.event.reservation.view.ProfileView;
import com.event.reservation.entity.ReservationStatus;
import java.util.stream.Collectors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.event.reservation.view.client.MyReservationsView; // Import the new view
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | EventGallery")
@CssImport(value = "./styles/client-dashboard-style.css")

@PermitAll

public class DashboardView extends VerticalLayout {

    private final ReservationService reservationService;
    private final SecurityService securityService;

    public DashboardView(ReservationService reservationService, SecurityService securityService) {
        this.reservationService = reservationService;
        this.securityService = securityService;

        addClassName("dashboard-view");
        setSpacing(true);
        setPadding(true);

        // Get Current User
        User currentUser = securityService.getAuthenticatedUser();

        if (currentUser != null) {
            // 1. Header
            add(createHeader(currentUser));

            // 2. Statistics Cards
            add(createStatsRow(currentUser));

            // 3. Main Content (Split: Upcoming Events & Shortcuts/Notifications)
            add(createMainContent(currentUser));
        }
    }

    private Component createHeader(User user) {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.setPadding(false);

        H2 title = new H2("Hello, " + user.getPrenom() + "!");
        title.addClassName("dashboard-title");

        Span subtitle = new Span("Welcome back. Here is your event overview.");
        subtitle.addClassName("dashboard-subtitle");

        header.add(title, subtitle);
        return header;
    }

    private Component createStatsRow(User user) {
        long totalReservations = reservationService.countReservationsByUser(user.getId());
        double totalSpent = reservationService.getTotalAmountByUser(user.getId());

        // FIX: Filter out ANNULE events before counting
        long upcomingCount = reservationService.getUpcomingReservations(user.getId()).stream()
                .filter(r -> r.getStatut() != ReservationStatus.ANNULEE)
                .count();

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.addClassName("dashboard-stats-row");

        // Card 1: Total Reservations
        row.add(createStatCard("Total Bookings", String.valueOf(totalReservations), VaadinIcon.TICKET, "card-blue"));

        // Card 2: Upcoming (Now shows correct count without cancelled events)
        row.add(createStatCard("Upcoming Events", String.valueOf(upcomingCount), VaadinIcon.CALENDAR_CLOCK, "card-green"));

        // Card 3: Total Spent
        row.add(createStatCard("Total Spent", String.format("%.2f MAD", totalSpent), VaadinIcon.WALLET, "card-purple"));

        return row;
    }
    private Div createStatCard(String label, String value, VaadinIcon icon, String colorClass) {
        Div card = new Div();
        card.addClassNames("stat-card", colorClass);

        Icon i = icon.create();
        i.addClassName("stat-icon");

        Span val = new Span(value);
        val.addClassName("stat-value");

        Span lbl = new Span(label);
        lbl.addClassName("stat-label");

        card.add(i, val, lbl);
        return card;
    }

    private Component createMainContent(User user) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.addClassName("dashboard-content-layout");

        // --- LEFT SECTION: Upcoming Events Grid ---
        VerticalLayout upcomingSection = new VerticalLayout();
        upcomingSection.addClassName("dashboard-section");
        upcomingSection.setWidth("70%");

        H4 sectionTitle = new H4("Your Upcoming Events");
        Grid<Reservation> grid = new Grid<>(Reservation.class, false);
        grid.addClassName("dashboard-grid");

        // 1. Get raw list
        List<Reservation> allUpcoming = reservationService.getUpcomingReservations(user.getId());

        // 2. FILTER: Exclude Cancelled events
        // (Assuming your enum value is 'ANNULE' or 'CANCELLED' - adjust if needed)
        List<Reservation> validUpcoming = allUpcoming.stream()
                .filter(r -> r.getStatut() != ReservationStatus.ANNULEE)
                .collect(Collectors.toList());

        grid.setItems(validUpcoming);

        grid.addColumn(r -> r.getEvenement().getTitre()).setHeader("Event").setAutoWidth(true);
        grid.addColumn(r -> r.getEvenement().getDateDebut().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"))).setHeader("Date");
        grid.addColumn(r -> r.getEvenement().getLieu()).setHeader("Location");
        // Optional: Add a column to show status (Confirmed/Pending)
        grid.addColumn(r -> r.getStatut()).setHeader("Status");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Check if the FILTERED list is empty
        if(validUpcoming.isEmpty()) {
            Div empty = new Div(new Span("No upcoming events found."));
            empty.addClassName("empty-state");
            upcomingSection.add(sectionTitle, empty);
        } else {
            upcomingSection.add(sectionTitle, grid);
        }

        // --- RIGHT SECTION: Shortcuts & Notifications ---
        VerticalLayout sideSection = new VerticalLayout();
        sideSection.addClassName("dashboard-sidebar");
        sideSection.setWidth("30%");

        // Shortcuts
        Div shortcutsCard = new Div();
        shortcutsCard.addClassName("dashboard-card-simple");
        H4 shortcutsTitle = new H4("Quick Actions");

        Button browseBtn = new Button("Browse Events", VaadinIcon.SEARCH.create());
        browseBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        browseBtn.setWidthFull();
        browseBtn.addClickListener(e -> UI.getCurrent().navigate(EventsView.class));

        Button myReservationsBtn = new Button("My Reservations", VaadinIcon.TICKET.create());
        myReservationsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        myReservationsBtn.setWidthFull();
        myReservationsBtn.addClickListener(e -> UI.getCurrent().navigate(MyReservationsView.class));

        Button profileBtn = new Button("Edit Profile", VaadinIcon.COG.create());
        profileBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        profileBtn.setWidthFull();

        profileBtn.addClickListener(e ->
                profileBtn.getUI().ifPresent(ui ->
                        ui.navigate(ProfileView.class)
                )
        );

        shortcutsCard.add(shortcutsTitle, browseBtn, myReservationsBtn, profileBtn);

        // Notifications
        Div notifyCard = new Div();
        notifyCard.addClassName("dashboard-card-simple");
        H4 notifyTitle = new H4("Notifications");

        Div notif1 = createNotificationItem("Welcome to the platform!", true);
        Div notif2 = createNotificationItem("Don't forget to complete your profile.", false);

        notifyCard.add(notifyTitle, notif1, notif2);

        sideSection.add(shortcutsCard, notifyCard);

        layout.add(upcomingSection, sideSection);
        layout.setFlexGrow(1, upcomingSection);

        return layout;
    }
    private Div createNotificationItem(String text, boolean important) {
        Div item = new Div();
        item.addClassName("notification-item");
        if(important) item.addClassName("important");
        item.setText(text);
        return item;
    }
}