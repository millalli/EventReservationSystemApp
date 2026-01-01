package com.event.reservation.view.admin;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.service.EventService;
import com.event.reservation.view.organizer.EventFormView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/events", layout = AdminLayout.class)
@PageTitle("Manage Events | Admin")
@RolesAllowed("ADMIN")
public class AllEventsManagementView extends VerticalLayout {

    private final EventService eventService;
    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final TextField searchField = new TextField();

    public AllEventsManagementView(EventService eventService) {
        this.eventService = eventService;

        addClassName("list-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();

        add(getToolbar(), grid);
        updateList();
    }

    private Component getToolbar() {
        searchField.setPlaceholder("Search by title or organizer...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.setWidth("300px");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());

        HorizontalLayout toolbar = new HorizontalLayout(searchField);
        toolbar.setWidthFull();
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Basic Info
        grid.addColumn(Event::getTitre).setHeader("Title").setAutoWidth(true).setSortable(true);

        // 2. Organizer Info (Important for Admin)
        grid.addColumn(event -> event.getOrganisateur().getNom() + " " + event.getOrganisateur().getPrenom())
                .setHeader("Organizer")
                .setAutoWidth(true)
                .setSortable(true);

        // 3. Date & Location
        grid.addColumn(event -> event.getDateDebut().toString() + " (" + event.getLieu() + ")")
                .setHeader("Date & Location")
                .setAutoWidth(true);

        // 4. Status Badge
        grid.addComponentColumn(event -> createStatusBadge(event.getStatut()))
                .setHeader("Status")
                .setAutoWidth(true)
                .setSortable(true);

        // 5. Action Buttons (The complex part)
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0); // Prevents actions from stretching too wide
    }

    private Component createStatusBadge(EventStatus status) {
        Span badge = new Span(status.name());
        String theme;

        if (status == EventStatus.PUBLIE) theme = "success";
        else if (status == EventStatus.ANNULE) theme = "error";
        else theme = "contrast"; // Brouillon

        badge.getElement().getThemeList().add("badge " + theme);
        return badge;
    }

    private Component createActionButtons(Event event) {
        HorizontalLayout actions = new HorizontalLayout();
        // Use standard spacing logic
        actions.getStyle().set("gap", "10px");

        // 1. EDIT Button (Link to Form)
        RouterLink editLink = new RouterLink(EventFormView.class, event.getId());
        editLink.add(new Icon(VaadinIcon.EDIT));
        editLink.addClassName("icon-link"); // Optional styling
        editLink.getElement().setAttribute("title", "Edit Event");

        // 2. PUBLISH / CANCEL Buttons (Dynamic)
        Button statusBtn = new Button();
        statusBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        if (event.getStatut() == EventStatus.BROUILLON || event.getStatut() == EventStatus.ANNULE) {
            // Action: Publish
            statusBtn.setIcon(new Icon(VaadinIcon.UPLOAD));
            statusBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            statusBtn.setTooltipText("Publish Event");
            statusBtn.addClickListener(e -> updateStatus(event, EventStatus.PUBLIE));
        } else {
            // Action: Cancel
            statusBtn.setIcon(new Icon(VaadinIcon.BAN));
            statusBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            statusBtn.setTooltipText("Cancel Event");
            statusBtn.addClickListener(e -> updateStatus(event, EventStatus.ANNULE));
        }

        // 3. DELETE Button
        Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteBtn.setTooltipText("Delete Permanently");
        deleteBtn.addClickListener(e -> deleteEvent(event));

        actions.add(editLink, statusBtn, deleteBtn);
        return actions;
    }

    private void updateStatus(Event event, EventStatus newStatus) {
        eventService.updateEventStatus(event.getId(), newStatus);
        updateList();
        Notification.show("Event status updated to " + newStatus)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void deleteEvent(Event event) {
        eventService.deleteEvent(event.getId());
        updateList();
        Notification.show("Event deleted")
                .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    private void updateList() {
        grid.setItems(eventService.findAllEvents(searchField.getValue()));
    }
}