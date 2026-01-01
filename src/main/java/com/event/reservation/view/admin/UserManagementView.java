package com.event.reservation.view.admin;

import com.event.reservation.entity.Role;
import com.event.reservation.entity.User;
import com.event.reservation.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/users", layout = AdminLayout.class)
@PageTitle("User Management | Admin")
@RolesAllowed("ADMIN")
public class UserManagementView extends VerticalLayout {

    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class, false);

    // Filters
    private final TextField searchField = new TextField();
    private final ComboBox<Role> roleFilter = new ComboBox<>("Role");
    private final ComboBox<String> statusFilter = new ComboBox<>("Status");

    public UserManagementView(UserService userService) {
        this.userService = userService;

        addClassName("user-management-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureFilters();

        add(createToolbar(), grid);
        updateList();
    }

    private Component createToolbar() {
        searchField.setPlaceholder("Search name or email...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, roleFilter, statusFilter);
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(Alignment.BASELINE);
        return toolbar;
    }

    private void configureFilters() {
        roleFilter.setItems(Role.values());
        roleFilter.addValueChangeListener(e -> updateList());
        roleFilter.setPlaceholder("Filter by Role");

        statusFilter.setItems("Active", "Inactive");
        statusFilter.addValueChangeListener(e -> updateList());
        statusFilter.setPlaceholder("Filter by Status");
    }

    private void configureGrid() {
        grid.addClassNames("user-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Name (Combine First and Last)
        grid.addColumn(user -> user.getNom() + " " + user.getPrenom())
                .setHeader("Full Name")
                .setSortable(true)
                .setAutoWidth(true);

        // 2. Email
        grid.addColumn(User::getEmail).setHeader("Email").setAutoWidth(true);

// 1. Role Filter
        roleFilter.setItems(Role.values());
        roleFilter.setPlaceholder("Filter by Role");
        roleFilter.setClearButtonVisible(true); // <--- Enables the "X" button
        roleFilter.addValueChangeListener(e -> updateList());

        // 2. Status Filter
        statusFilter.setItems("Active", "Inactive");
        statusFilter.setPlaceholder("Filter by Status");
        statusFilter.setClearButtonVisible(true); // <--- Enables the "X" button
        statusFilter.addValueChangeListener(e -> updateList());
        // 5. Actions
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setAutoWidth(true);
    }

    private Component createActionButtons(User user) {
        HorizontalLayout actions = new HorizontalLayout();

        // Action: Change Role
        Button roleBtn = new Button(new Icon(VaadinIcon.USER_CARD));
        roleBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        roleBtn.setTooltipText("Change Role");
        roleBtn.addClickListener(e -> openRoleDialog(user));

        // Action: Toggle Status (Block/Unblock)
        Button toggleBtn = new Button();
        toggleBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        if (user.isEnabled()) {
            toggleBtn.setIcon(new Icon(VaadinIcon.BAN));
            toggleBtn.setTooltipText("Deactivate User");
            toggleBtn.addClassName("error-text"); // Helper CSS class if needed
            toggleBtn.addClickListener(e -> toggleStatus(user));
        } else {
            toggleBtn.setIcon(new Icon(VaadinIcon.CHECK));
            toggleBtn.setTooltipText("Activate User");
            toggleBtn.addClassName("success-text");
            toggleBtn.addClickListener(e -> toggleStatus(user));
        }

        actions.add(roleBtn, toggleBtn);
        return actions;
    }

    private void toggleStatus(User user) {
        if (user.getRole() == Role.ADMIN) {
            Notification.show("Cannot deactivate an Admin!")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        userService.toggleUserStatus(user.getId());
        updateList();
        Notification.show("User status updated").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void openRoleDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Change Role for " + user.getPrenom());

        ComboBox<Role> roleSelect = new ComboBox<>("Select New Role");
        roleSelect.setItems(Role.values());
        roleSelect.setValue(user.getRole());

        Button saveButton = new Button("Save", e -> {
            userService.updateUserRole(user.getId(), roleSelect.getValue());
            updateList();
            dialog.close();
            Notification.show("Role updated successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout layout = new VerticalLayout(roleSelect);
        dialog.add(layout);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void updateList() {
        Boolean status = null;
        if (statusFilter.getValue() != null) {
            status = statusFilter.getValue().equals("Active");
        }

        grid.setItems(userService.searchUsers(
                searchField.getValue(),
                roleFilter.getValue(),
                status
        ));
    }
}