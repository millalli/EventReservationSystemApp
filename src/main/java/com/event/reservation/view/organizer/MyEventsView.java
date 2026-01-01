package com.event.reservation.view.organizer;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.EventCategory;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.entity.User;
import com.event.reservation.service.EventService;
import com.event.reservation.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import com.event.reservation.view.organizer.OrganizerLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "organizer/events", layout = OrganizerLayout.class)
@PageTitle("My Events | Organizer")
@RolesAllowed("ORGANIZER")
public class MyEventsView extends VerticalLayout {

    private final EventService eventService;
    private final SecurityService userAccessService;

    private Grid<Event> grid = new Grid<>(Event.class, false);
    private GridListDataView<Event> dataView;
    private User currentUser;

    // UI Filters
    private TextField searchField;
    private ComboBox<EventStatus> statusFilter;
    private ComboBox<EventCategory> categoryFilter;
    private DatePicker dateFilter;

    // Stat Cards Container
    private HorizontalLayout statsContainer;

    public MyEventsView(EventService eventService, SecurityService userAccessService) {
        this.eventService = eventService;
        this.userAccessService = userAccessService;

        addClassName("my-events-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        currentUser = userAccessService.getAuthenticatedUser();
        if (currentUser == null) return;

        // 1. Top Section: Stat Cards (Replaces Charts)
        statsContainer = new HorizontalLayout();
        statsContainer.setWidthFull();
        statsContainer.setSpacing(true);
        add(statsContainer);

        // 2. Toolbar & Filters
        add(createToolbar());

        // 3. Grid
        configureGrid();
        add(grid);

        // 4. Load Data
        updateView();
    }

    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H2 title = new H2("My Events");
        title.getStyle().set("margin", "0");

        HorizontalLayout filters = new HorizontalLayout();
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);

        searchField = new TextField("Search");
        searchField.setPlaceholder("Title...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> refreshFilter());

        statusFilter = new ComboBox<>("Status");
        statusFilter.setItems(EventStatus.values());
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> refreshFilter());

        categoryFilter = new ComboBox<>("Category");
        categoryFilter.setItems(EventCategory.values());
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.addValueChangeListener(e -> refreshFilter());

        dateFilter = new DatePicker("After Date");
        dateFilter.addValueChangeListener(e -> refreshFilter());

        Button clearBtn = new Button(new Icon(VaadinIcon.ERASER));
        clearBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearBtn.addClickListener(e -> {
            searchField.clear();
            statusFilter.clear();
            categoryFilter.clear();
            dateFilter.clear();
        });

        filters.add(searchField, statusFilter, categoryFilter, dateFilter, clearBtn);

        Button addEventButton = new Button("Create Event", new Icon(VaadinIcon.PLUS));
        addEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEventButton.addClickListener(e -> UI.getCurrent().navigate("organizer/events/new"));

        VerticalLayout leftSide = new VerticalLayout(title, filters);
        leftSide.setPadding(false);
        leftSide.setSpacing(true);

        toolbar.add(leftSide, addEventButton);
        return toolbar;
    }

    private void refreshFilter() {
        if (dataView != null) dataView.refreshAll();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // 1. Info
        grid.addColumn(new ComponentRenderer<>(event -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(false);
            layout.setSpacing(false);

            Span title = new Span(event.getTitre());
            title.getStyle().set("font-weight", "bold");

            String catName = event.getCategorie() != null ? String.valueOf(event.getCategorie()) : "-";
            Span category = new Span(catName);
            category.getStyle().set("font-size", "0.85em").set("color", "var(--lumo-secondary-text-color)");

            layout.add(title, category);
            return layout;
        })).setHeader("Event").setAutoWidth(true);

        // 2. Date
        grid.addColumn(event -> event.getDateDebut().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")))
                .setHeader("Date").setAutoWidth(true);

        // 3. Status
        grid.addComponentColumn(event -> {
            Span badge = new Span(event.getStatut().name());
            String theme = switch (event.getStatut()) {
                case PUBLIE -> "success";
                case BROUILLON -> "contrast";
                case ANNULE -> "error";
                default -> "primary";
            };
            badge.getElement().getThemeList().add("badge " + theme);
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        // 4. Capacity
        grid.addComponentColumn(event -> {
            int total = event.getCapaciteMax();

            // 1. Get available spots from your Service
            int available = eventService.countAvailablePlaces(event.getId());

            // 2. Calculate sold spots (Total - Available)
            int currentReserved = total - available;

            return createFillingIndicator(currentReserved, total);
        }).setHeader("Sales").setAutoWidth(true);

        // 5. Actions
        grid.addComponentColumn(this::createActionsMenu).setHeader("Actions");

        grid.addAttachListener(e -> {
            dataView = grid.getListDataView();
            dataView.setFilter(event -> {
                boolean matchesSearch = searchField.getValue().isEmpty() ||
                        event.getTitre().toLowerCase().contains(searchField.getValue().toLowerCase());
                boolean matchesStatus = statusFilter.getValue() == null ||
                        event.getStatut() == statusFilter.getValue();
                boolean matchesCategory = categoryFilter.getValue() == null ||
                        event.getCategorie() == categoryFilter.getValue();
                boolean matchesDate = dateFilter.getValue() == null ||
                        (event.getDateDebut() != null && event.getDateDebut().toLocalDate().isAfter(dateFilter.getValue().minusDays(1)));
                return matchesSearch && matchesStatus && matchesCategory && matchesDate;
            });
        });
    }

    // --- REPLACEMENT: Stat Cards instead of Charts ---
    private void updateView() {
        List<Event> events = eventService.findAllEventsByOrganizer(currentUser.getId());
        grid.setItems(events);

        statsContainer.removeAll();

        long total = events.size();
        long published = events.stream().filter(e -> e.getStatut() == EventStatus.PUBLIE).count();
        long drafts = events.stream().filter(e -> e.getStatut() == EventStatus.BROUILLON).count();

        statsContainer.add(
                createStatCard("Total Events", String.valueOf(total), "blue"),
                createStatCard("Published", String.valueOf(published), "green"),
                createStatCard("Drafts", String.valueOf(drafts), "grey")
        );
    }

    private Div createStatCard(String title, String count, String color) {
        Div card = new Div();
        card.getStyle().set("background-color", "white")
                .set("border-radius", "10px")
                .set("padding", "20px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)")
                .set("flex", "1")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center");

        // Color Accents
        String colorCode = switch(color) {
            case "green" -> "#10b981";
            case "blue" -> "#3b82f6";
            default -> "#64748b";
        };
        card.getStyle().set("border-top", "4px solid " + colorCode);

        Span countSpan = new Span(count);
        countSpan.getStyle().set("font-size", "2rem").set("font-weight", "bold").set("color", colorCode);

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("color", "var(--lumo-secondary-text-color)");

        card.add(countSpan, titleSpan);
        return card;
    }

    // --- Helpers remain the same ---
    private Component createFillingIndicator(int reserved, int total) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidth("120px");
        Span text = new Span(reserved + " / " + total + " sold");
        text.getStyle().set("font-size", "0.8em");
        Div progressBarBackground = new Div();
        progressBarBackground.setWidthFull();
        progressBarBackground.setHeight("6px");
        progressBarBackground.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
        progressBarBackground.getStyle().set("border-radius", "3px");
        progressBarBackground.getStyle().set("margin-top", "4px");
        Div progressBarFill = new Div();
        double percentage = total > 0 ? ((double) reserved / total) * 100 : 0;
        progressBarFill.setWidth(percentage + "%");
        progressBarFill.setHeight("100%");
        progressBarFill.getStyle().set("background-color", "var(--lumo-primary-color)");
        progressBarFill.getStyle().set("border-radius", "3px");
        progressBarBackground.add(progressBarFill);
        layout.add(text, progressBarBackground);
        return layout;
    }

    private Component createActionsMenu(Event event) {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem item = menuBar.addItem(new Icon(VaadinIcon.ELLIPSIS_DOTS_V));
        SubMenu subMenu = item.getSubMenu();
        subMenu.addItem("View Details", e ->
                UI.getCurrent().navigate(EventReservationsView.class, event.getId())
        );
        if (event.getStatut() != EventStatus.ANNULE) {
            subMenu.addItem("Edit", e -> UI.getCurrent().navigate("organizer/events/edit/" + event.getId()));
        }
        if (event.getStatut() == EventStatus.BROUILLON) {
            subMenu.addItem("Publish", e -> {
                eventService.publishEvent(event.getId());
                updateView();
                showNotification("Event published", "success");
            });
        }
        if (event.getStatut() == EventStatus.PUBLIE) {
            MenuItem cancelItem = subMenu.addItem("Cancel Event", e -> {
                eventService.cancelEvent(event.getId());
                updateView();
                showNotification("Event cancelled", "error");
            });
            cancelItem.getStyle().set("color", "var(--lumo-error-text-color)");
        }
        if (event.getStatut() == EventStatus.BROUILLON || event.getStatut() == EventStatus.ANNULE) {
            MenuItem deleteItem = subMenu.addItem("Delete", e -> {
                eventService.deleteEvent(event.getId());
                updateView();
                showNotification("Event deleted", "contrast");
            });
            deleteItem.getStyle().set("color", "var(--lumo-error-text-color)");
        }
        return menuBar;
    }

    private void showNotification(String message, String theme) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.valueOf("LUMO_" + theme.toUpperCase()));
        notification.setPosition(Notification.Position.BOTTOM_END);
        notification.setDuration(3000);
    }
}