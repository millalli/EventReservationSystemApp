package com.event.reservation.view.admin;

import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.ReservationStatus;
import com.event.reservation.service.ReservationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Route(value = "admin/reservations", layout = AdminLayout.class)
@PageTitle("All Reservations | Admin")
@RolesAllowed("ADMIN")
public class AllReservationsView extends VerticalLayout {

    private final ReservationService reservationService;
    private final Grid<Reservation> grid = new Grid<>(Reservation.class, false);

    // Filters
    private final TextField searchField = new TextField();
    private final ComboBox<ReservationStatus> statusFilter = new ComboBox<>("Status");
    private final DatePicker startDate = new DatePicker("From");
    private final DatePicker endDate = new DatePicker("To");

    public AllReservationsView(ReservationService reservationService) {
        this.reservationService = reservationService;

        addClassName("all-reservations-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // 1. Top Statistics Board
        add(createStatsBoard());

        // 2. Main Content
        configureGrid();
        configureFilters();

        add(createToolbar(), grid);
        updateList();
    }

    private Component createStatsBoard() {
        long confirmed = reservationService.countReservations(ReservationStatus.CONFIRMEE);
        long cancelled = reservationService.countReservations(ReservationStatus.ANNULEE);
        Double revenue = reservationService.getTotalRevenue();

        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.addClassName("stats-board"); // Use CSS to style cards if you want

        stats.add(createStatCard("Confirmed", String.valueOf(confirmed), "success"));
        stats.add(createStatCard("Cancelled", String.valueOf(cancelled), "error"));
        stats.add(createStatCard("Total Revenue", revenue + " MAD", "primary"));

        return stats;
    }

    private VerticalLayout createStatCard(String title, String value, String theme) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.setPadding(true);
        card.getStyle().set("background-color", "var(--lumo-base-color)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("border-radius", "var(--lumo-border-radius-m)");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size", "var(--lumo-font-size-s)");

        H2 valueSpan = new H2(value);
        valueSpan.getStyle().set("color", "var(--lumo-" + theme + "-color)").set("margin", "0");

        card.add(titleSpan, valueSpan);
        return card;
    }

    private void configureFilters() {
        statusFilter.setItems(ReservationStatus.values());
        statusFilter.setPlaceholder("Status");
        statusFilter.setClearButtonVisible(true);

        searchField.setPlaceholder("Code, Event, or Client...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        // Listeners
        searchField.addValueChangeListener(e -> updateList());
        statusFilter.addValueChangeListener(e -> updateList());
        startDate.addValueChangeListener(e -> updateList());
        endDate.addValueChangeListener(e -> updateList());
    }

    private Component createToolbar() {
        HorizontalLayout filterRow = new HorizontalLayout(searchField, statusFilter, startDate, endDate);
        filterRow.setAlignItems(Alignment.BASELINE);

        // Export Button
        Button exportBtn = new Button("Export CSV", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Create the download link (Anchor) wrapping the button
        Anchor downloadLink = new Anchor(new StreamResource("reservations.csv",
                () -> new ByteArrayInputStream(reservationService.generateCSVExport().getBytes(StandardCharsets.UTF_8))),
                "");
        downloadLink.add(exportBtn);
        downloadLink.getElement().setAttribute("download", true);

        HorizontalLayout toolbar = new HorizontalLayout(filterRow, downloadLink);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.addClassName("toolbar");

        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // FIX: getCode() -> getCodeReservation()
        grid.addColumn(Reservation::getCodeReservation)
                .setHeader("Code")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(r -> r.getEvenement().getTitre())
                .setHeader("Event")
                .setAutoWidth(true);

        // FIX: getClient() -> getUtilisateur()
        grid.addColumn(r -> r.getUtilisateur().getNom() + " " + r.getUtilisateur().getPrenom())
                .setHeader("Client")
                .setAutoWidth(true);

        grid.addColumn(r -> r.getDateReservation().toLocalDate().toString())
                .setHeader("Date")
                .setSortable(true);

        // FIX: getPrixTotal() -> getMontantTotal()
        grid.addColumn(r -> r.getMontantTotal() + " MAD")
                .setHeader("Total")
                .setAutoWidth(true);

        grid.addComponentColumn(r -> {
            Span badge = new Span(r.getStatut().name());
            String theme = r.getStatut() == ReservationStatus.CONFIRMEE ? "success" :
                    r.getStatut() == ReservationStatus.ANNULEE ? "error" : "contrast";
            badge.getElement().getThemeList().add("badge " + theme);
            return badge;
        }).setHeader("Status").setAutoWidth(true);
    }
    private void updateList() {
        grid.setItems(reservationService.getAllReservations(
                searchField.getValue(),
                statusFilter.getValue(),
                startDate.getValue() != null ? startDate.getValue().atStartOfDay() : null,
                endDate.getValue() != null ? endDate.getValue().atStartOfDay() : null
        ));
    }
}