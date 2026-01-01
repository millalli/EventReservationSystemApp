package com.event.reservation.view.client;

import com.event.reservation.entity.Reservation;
import com.event.reservation.entity.ReservationStatus;
import com.event.reservation.entity.User;
import com.event.reservation.layout.MainLayout;
import com.event.reservation.security.SecurityService;
import com.event.reservation.service.ReservationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "my-reservations", layout = MainLayout.class)
@PageTitle("My Reservations | EventApp")
@PermitAll
public class MyReservationsView extends VerticalLayout {

    private final ReservationService reservationService;
    private final SecurityService securityService;

    private Grid<Reservation> grid = new Grid<>(Reservation.class, false);
    private TextField searchField = new TextField();
    private ComboBox<ReservationStatus> statusFilter = new ComboBox<>();
    private ListDataProvider<Reservation> dataProvider;

    public MyReservationsView(ReservationService reservationService, SecurityService securityService) {
        this.reservationService = reservationService;
        this.securityService = securityService;

        addClassName("my-reservations-view");
        setSizeFull();
        setPadding(true);

        configureGrid();

        add(new H2("My Reservations"), createToolbar(), grid);

        updateList();
    }

    private Component createToolbar() {
        searchField.setPlaceholder("Search by Code...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> applyFilters());

        statusFilter.setPlaceholder("Filter by Status");
        statusFilter.setItems(ReservationStatus.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> applyFilters());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, statusFilter);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("reservation-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Code (SAFE)
        grid.addColumn(new ComponentRenderer<>(reservation -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.CENTER);

            String codeText = reservation.getCodeReservation() != null ? reservation.getCodeReservation() : "N/A";
            Span code = new Span(codeText);
            code.getStyle().set("font-weight", "bold");
            layout.add(code);

            // Safe Check: Event must exist to check date
            if (reservation.getEvenement() != null
                    && reservation.getEvenement().getDateDebut().isAfter(LocalDateTime.now())
                    && reservation.getStatut() != ReservationStatus.ANNULEE) {

                Icon upcomingIcon = VaadinIcon.CLOCK.create();
                upcomingIcon.setColor("green");
                upcomingIcon.setSize("12px");
                layout.add(upcomingIcon);
            }
            return layout;
        })).setHeader("Code").setAutoWidth(true);

        // 2. Event Name (SAFE)
        grid.addColumn(r -> r.getEvenement() != null ? r.getEvenement().getTitre() : "Unknown Event")
                .setHeader("Event").setSortable(true);

        // 3. Date (SAFE)
        grid.addColumn(r -> {
            if (r.getEvenement() != null && r.getEvenement().getDateDebut() != null) {
                return r.getEvenement().getDateDebut().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
            }
            return "-";
        }).setHeader("Date").setSortable(true);

        // 4. Places
        grid.addColumn(Reservation::getNombrePlaces).setHeader("Places").setTextAlign(ColumnTextAlign.CENTER);

        // 5. Total
        grid.addColumn(r -> String.format("%.2f MAD", r.getMontantTotal())).setHeader("Total");

        // 6. Status
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge)).setHeader("Status");

        // 7. Actions (SAFE)
        grid.addComponentColumn(reservation -> {
            Button detailsBtn = new Button(VaadinIcon.EYE.create(), e -> showDetails(reservation));
            detailsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button cancelBtn = new Button(VaadinIcon.TRASH.create(), e -> confirmCancellation(reservation));
            cancelBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

            // Safe Check: Hide cancel button if event is null or date passed
            boolean isCancellable = false;
            if (reservation.getStatut() != ReservationStatus.ANNULEE && reservation.getEvenement() != null) {
                if(reservation.getEvenement().getDateDebut().isAfter(LocalDateTime.now())) {
                    isCancellable = true;
                }
            }
            cancelBtn.setVisible(isCancellable);

            return new HorizontalLayout(detailsBtn, cancelBtn);
        }).setHeader("Actions");
    }

    private Span createStatusBadge(Reservation reservation) {
        ReservationStatus status = reservation.getStatut();
        if (status == null) return new Span("Unknown"); // Safe check

        Span badge = new Span(status.name());
        badge.getElement().getThemeList().add("badge");

        if (status == ReservationStatus.CONFIRMEE) {
            badge.getElement().getThemeList().add("success");
        } else if (status == ReservationStatus.EN_ATTENTE) {
            badge.getElement().getThemeList().add("contrast");
        } else if (status == ReservationStatus.ANNULEE) {
            badge.getElement().getThemeList().add("error");
        }
        return badge;
    }

    private void updateList() {
        User user = securityService.getAuthenticatedUser();
        if (user != null) {
            // This might return reservations with NULL events if DB is dirty
            List<Reservation> reservations = reservationService.getReservationsByUser(user.getId());
            dataProvider = new ListDataProvider<>(reservations);
            grid.setDataProvider(dataProvider);
        }
    }

    private void applyFilters() {
        if (dataProvider == null) return;
        dataProvider.setFilter(reservation -> {
            boolean matchesCode = true;
            boolean matchesStatus = true;

            String searchTerm = searchField.getValue();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                // Safe check for null code
                String code = reservation.getCodeReservation() != null ? reservation.getCodeReservation() : "";
                matchesCode = code.toLowerCase().contains(searchTerm.toLowerCase());
            }

            if (statusFilter.getValue() != null) {
                matchesStatus = reservation.getStatut() == statusFilter.getValue();
            }
            return matchesCode && matchesStatus;
        });
    }

    private void showDetails(Reservation reservation) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reservation Details");
        VerticalLayout layout = new VerticalLayout();

        // SAFE CHECKS
        String title = reservation.getEvenement() != null ? reservation.getEvenement().getTitre() : "Unknown Event";
        String loc = reservation.getEvenement() != null ? reservation.getEvenement().getLieu() : "Unknown Location";

        layout.add(new Span("Event: " + title));
        layout.add(new Span("Location: " + loc));
        layout.add(new Span("Booked on: " + reservation.getDateReservation().format(DateTimeFormatter.ISO_DATE)));

        Button closeButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(closeButton);
        dialog.add(layout);
        dialog.open();
    }

    private void confirmCancellation(Reservation reservation) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Cancel Reservation?");
        confirmDialog.add("Are you sure you want to cancel this reservation? This action cannot be undone.");

        Button confirmBtn = new Button("Yes, Cancel", e -> {
            try {
                reservationService.annuler(reservation.getId());
                Notification.show("Reservation cancelled successfully.")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                updateList();
                confirmDialog.close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancelBtn = new Button("No", e -> confirmDialog.close());
        confirmDialog.getFooter().add(cancelBtn, confirmBtn);
        confirmDialog.open();
    }
    //add charts later
}