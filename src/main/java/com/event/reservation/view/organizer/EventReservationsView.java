package com.event.reservation.view.organizer;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.ReservationStatus;
import com.event.reservation.view.organizer.OrganizerLayout;
import com.event.reservation.service.EventService;
import com.event.reservation.service.ReservationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign; // Fixed Import
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "organizer/event/reservations", layout = OrganizerLayout.class)
@PageTitle("Reservations | Event Gallery")
@RolesAllowed("ORGANIZER")
public class EventReservationsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final EventService eventService;
    private final ReservationService reservationService;

    private Event event;
    private List<Reservation> allReservations;

    // UI Components
    private Grid<Reservation> grid = new Grid<>(Reservation.class);
    private TextField searchField = new TextField();
    private ComboBox<ReservationStatus> statusFilter = new ComboBox<>();

    // Stats Components
    private Span totalReservationsVal = new Span("0");
    private Span totalTicketsVal = new Span("0");
    private Span totalRevenueVal = new Span("0.00 €");

    public EventReservationsView(EventService eventService, ReservationService reservationService) {
        this.eventService = eventService;
        this.reservationService = reservationService;

        addClassName("reservations-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long eventId) {
        if(eventId == null) return;

        this.event = eventService.findById(eventId).orElse(null);

        if (this.event == null) {
            Notification.show("Event not found");
            UI.getCurrent().navigate(MyEventsView.class);
            return;
        }

        removeAll();
        add(
                createHeader(),
                createStatsBoard(),
                createToolbar(),
                grid
        );

        updateList();
    }

    private Component createHeader() {
        Button backBtn = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.addClickListener(e -> UI.getCurrent().navigate(MyEventsView.class));

        H2 title = new H2("Reservations: " + event.getTitre());
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout header = new HorizontalLayout(backBtn, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }

    private Component createStatsBoard() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        // Fixed: Use class names instead of setGap for compatibility
        stats.addClassNames(LumoUtility.Gap.MEDIUM);

        stats.add(
                // Fixed: VaadinIcon.USERS (uppercase)
                createStatCard("Total Reservations", totalReservationsVal, VaadinIcon.USERS),
                createStatCard("Tickets Sold", totalTicketsVal, VaadinIcon.TICKET),
                createStatCard("Total Revenue", totalRevenueVal, VaadinIcon.MONEY)
        );

        return stats;
    }

    private Component createStatCard(String label, Span valueSpan, VaadinIcon icon) {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Gap.MEDIUM
        );
        card.setWidth("30%");

        Icon i = icon.create();
        i.addClassNames(LumoUtility.IconSize.LARGE, LumoUtility.TextColor.PRIMARY);

        VerticalLayout text = new VerticalLayout();
        text.setSpacing(false);
        text.setPadding(false);

        Span lbl = new Span(label);
        lbl.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        valueSpan.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.BOLD);

        text.add(valueSpan, lbl);
        card.add(i, text);
        return card;
    }

    private Component createToolbar() {
        searchField.setPlaceholder("Search name or code...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterList());

        statusFilter.setPlaceholder("Filter by Status");
        statusFilter.setItems(ReservationStatus.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> filterList());

        Button exportBtn = new Button("Export CSV", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addClickListener(e -> Notification.show("Export functionality coming soon!"));

        HorizontalLayout toolbar = new HorizontalLayout(searchField, statusFilter, exportBtn);
        toolbar.addClassNames(LumoUtility.Margin.Bottom.SMALL);
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.removeAllColumns();

        grid.addColumn(Reservation::getId).setHeader("ID").setWidth("60px").setFlexGrow(0);

        // Fixed: getUser() -> getUtilisateur()
        grid.addColumn(r -> r.getUtilisateur().getNom() + " " + r.getUtilisateur().getPrenom())
                .setHeader("Client Name").setSortable(true);

        grid.addColumn(r -> r.getDateReservation().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")))
                .setHeader("Date").setSortable(true);

        // Fixed: ColumnTextAlign import is now included above
        grid.addColumn(Reservation::getNombrePlaces)
                .setHeader("Qty")
                .setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(r -> r.getMontantTotal() + " €").setHeader("Total").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status").setSortable(true);

        grid.addComponentColumn(this::createActions).setHeader("Actions");
    }

    private Component createStatusBadge(Reservation reservation) {
        Span badge = new Span(reservation.getStatut().name());
        badge.getElement().getThemeList().add("badge");

        if (reservation.getStatut() == ReservationStatus.CONFIRMEE) {
            badge.getElement().getThemeList().add("success");
        } else if (reservation.getStatut() == ReservationStatus.ANNULEE) {
            badge.getElement().getThemeList().add("error");
        } else {
            badge.getElement().getThemeList().add("contrast");
        }
        return badge;
    }

    private Component createActions(Reservation reservation) {
        Button detailsBtn = new Button(new Icon(VaadinIcon.EYE));
        detailsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        detailsBtn.setTooltipText("View Details");
        detailsBtn.addClickListener(e -> showDetailsDialog(reservation));

        Button confirmBtn = new Button(new Icon(VaadinIcon.CHECK));
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        confirmBtn.setTooltipText("Confirm Reservation");

        if(reservation.getStatut() != ReservationStatus.EN_ATTENTE) {
            confirmBtn.setVisible(false);
        }

        // Fixed: Use confirmerReservation() instead of updateStatus()
        confirmBtn.addClickListener(e -> {
            reservationService.confirmerReservation(reservation.getId());
            Notification.show("Reservation Confirmed");
            updateList();
        });

        return new HorizontalLayout(detailsBtn, confirmBtn);
    }

    private void updateList() {
        // Fixed: This method must exist in ReservationService interface
        allReservations = reservationService.findByEventId(event.getId());

        calculateStats();
        filterList();
    }

    private void filterList() {
        if (allReservations == null) return;

        List<Reservation> filtered = allReservations.stream()
                .filter(r -> {
                    if (statusFilter.getValue() != null && r.getStatut() != statusFilter.getValue()) {
                        return false;
                    }
                    String searchTerm = searchField.getValue().toLowerCase();
                    if (!searchTerm.isEmpty()) {
                        // Fixed: getUser() -> getUtilisateur()
                        String clientName = (r.getUtilisateur().getNom() + " " + r.getUtilisateur().getPrenom()).toLowerCase();
                        return clientName.contains(searchTerm);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        grid.setItems(filtered);
    }

    private void calculateStats() {
        if (allReservations == null) return;

        int count = allReservations.size();
        int tickets = allReservations.stream()
                .filter(r -> r.getStatut() != ReservationStatus.ANNULEE)
                .mapToInt(Reservation::getNombrePlaces).sum();

        double revenue = allReservations.stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .mapToDouble(Reservation::getMontantTotal).sum();

        totalReservationsVal.setText(String.valueOf(count));
        totalTicketsVal.setText(String.valueOf(tickets));
        totalRevenueVal.setText(String.format("%.2f €", revenue));
    }

    private void showDetailsDialog(Reservation r) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reservation Details #" + r.getId());

        VerticalLayout layout = new VerticalLayout();
        // Fixed: getUser() -> getUtilisateur()
        layout.add(new Span("Client: " + r.getUtilisateur().getNom() + " " + r.getUtilisateur().getPrenom()));
        layout.add(new Span("Email: " + r.getUtilisateur().getEmail()));
        layout.add(new Span("Date: " + r.getDateReservation()));
        layout.add(new Span("Status: " + r.getStatut()));
        layout.add(new Hr());
        layout.add(new H4("Amount: " + r.getMontantTotal() + " €"));

        Button close = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(close);
        dialog.add(layout);
        dialog.open();
    }
}