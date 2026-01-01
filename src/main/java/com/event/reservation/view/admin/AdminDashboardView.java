package com.event.reservation.view.admin;

import com.event.reservation.service.DashboardService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.Map;

@Route(value = "admin/dashboard", layout = AdminLayout.class)
@PageTitle("Admin Dashboard | Event Gallery")
@RolesAllowed("ADMIN")
public class AdminDashboardView extends VerticalLayout {

    private final DashboardService dashboardService;

    public AdminDashboardView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;

        addClassName("dashboard-view");
        setPadding(true);
        setSpacing(true);

        add(new H2("Platform Overview"));

        // Create the layout for cards
        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cardsLayout.getStyle().set("gap", "20px");
        cardsLayout.setWidthFull();

        // Fetch Data
        Map<String, Long> userStats = dashboardService.getUserStats();
        Map<String, Long> eventStats = dashboardService.getEventStats();
        long totalReservations = dashboardService.getTotalReservations();
        Double revenue = dashboardService.getTotalRevenue();

        // 1. User Stats Card
        cardsLayout.add(createCard("Users", VaadinIcon.USERS,
                userStats.get("Total").toString(),
                "Clients: " + userStats.get("Clients") + " | Organizers: " + userStats.get("Organizers"),
                "blue"));

        // 2. Event Stats Card
        cardsLayout.add(createCard("Events", VaadinIcon.CALENDAR,
                eventStats.get("Total").toString(),
                "Published: " + eventStats.get("Published") + " | Drafts: " + eventStats.get("Drafts"),
                "purple"));

        // 3. Revenue Card
        cardsLayout.add(createCard("Total Revenue", VaadinIcon.MONEY,
                "$" + String.format("%.2f", revenue),
                "From confirmed reservations",
                "green"));

        // 4. Reservations Card
        cardsLayout.add(createCard("Reservations", VaadinIcon.TICKET,
                String.valueOf(totalReservations),
                "Total bookings processed",
                "orange"));

        add(cardsLayout);
    }

    private Component createCard(String title, VaadinIcon icon, String number, String subtitle, String colorTheme) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("dashboard-card");
        card.getStyle().set("border-left", "5px solid " + getColorCode(colorTheme));

        // Icon Header
        Icon i = icon.create();
        i.addClassName(LumoUtility.IconSize.LARGE);
        i.setColor(getColorCode(colorTheme));

        H3 statNumber = new H3(number);
        statNumber.addClassName("stat-number");

        Span statTitle = new Span(title);
        statTitle.addClassName("stat-title");

        Span statSubtitle = new Span(subtitle);
        statSubtitle.addClassName("stat-subtitle");

        card.add(i, statTitle, statNumber, statSubtitle);
        return card;
    }

    private String getColorCode(String theme) {
        switch (theme) {
            case "blue": return "#0099ff";
            case "purple": return "#9b59b6";
            case "green": return "#2ecc71";
            case "orange": return "#e67e22";
            default: return "#bdc3c7";
        }
    }
}