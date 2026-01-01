package com.event.reservation.view;

import com.event.reservation.entity.User;
import com.event.reservation.layout.MainLayout;
import com.event.reservation.repository.ReservationRepository;
import com.event.reservation.security.SecurityService;
import com.event.reservation.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("My Profile | Event Gallery")
@PermitAll
public class ProfileView extends VerticalLayout {

    private final UserService userService;
    private final SecurityService securityService;
    private final ReservationRepository reservationRepository;

    private User currentUser;
    private final Binder<User> binder = new Binder<>(User.class);

    public ProfileView(UserService userService,
                       SecurityService securityService,
                       ReservationRepository reservationRepository) {
        this.userService = userService;
        this.securityService = securityService;
        this.reservationRepository = reservationRepository;

        // Load the logged-in user
        this.currentUser = securityService.getAuthenticatedUser();

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        addClassName("profile-view");
        setAlignItems(Alignment.CENTER);

        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        add(
                createHeader(),
                createStatsSection(),
                createPersonalDetailsSection(),
                createPasswordSection(),
                createDangerZone()
        );
    }

    private VerticalLayout createHeader() {
        H2 title = new H2("My Profile");
        Span subtitle = new Span("Manage your account settings");
        subtitle.getStyle().set("color", "gray");
        VerticalLayout header = new VerticalLayout(title, subtitle);
        header.setAlignItems(Alignment.CENTER);
        return header;
    }

    private HorizontalLayout createStatsSection() {
        long totalReservations = reservationRepository.countByUtilisateurId(currentUser.getId());
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        statsLayout.setSpacing(true);
        statsLayout.add(createStatCard("Total Reservations", String.valueOf(totalReservations), VaadinIcon.CALENDAR_USER));
        return statsLayout;
    }

    private VerticalLayout createStatCard(String label, String value, VaadinIcon icon) {
        Icon i = icon.create();
        i.setColor("#007bff");
        i.setSize("30px");
        H3 val = new H3(value);
        val.getStyle().set("margin", "0");
        Span lbl = new Span(label);
        lbl.getStyle().set("color", "gray").set("font-size", "0.9em");
        VerticalLayout card = new VerticalLayout(i, val, lbl);
        card.setAlignItems(Alignment.CENTER);
        card.setWidth("200px");
        card.getStyle().set("background", "#f8f9fa").set("border-radius", "10px").set("padding", "20px");
        return card;
    }

    private VerticalLayout createPersonalDetailsSection() {
        H4 title = new H4("Personal Information");

        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        TextField phone = new TextField("Phone Number");

        // Bind fields
        binder.forField(firstName).bind(User::getPrenom, User::setPrenom);
        binder.forField(lastName).bind(User::getNom, User::setNom);
        binder.forField(email).bind(User::getEmail, User::setEmail);
        binder.forField(phone).bind(User::getTelephone, User::setTelephone);

        binder.setBean(currentUser);

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, email, phone);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));

        Button saveButton = new Button("Update Profile");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.writeBeanIfValid(currentUser)) {
                // FIXED: Using updateProfile instead of updateUser
                userService.updateProfile(currentUser.getId(), currentUser);

                Notification.show("Profile updated successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        VerticalLayout section = new VerticalLayout(title, formLayout, saveButton);
        section.getStyle().set("border", "1px solid #e0e0e0").set("border-radius", "8px").set("padding", "20px");
        section.setWidthFull();
        return section;
    }

    private VerticalLayout createPasswordSection() {
        H4 title = new H4("Change Password");

        // FIXED: Added Old Password field because your Service requires it
        PasswordField oldPassword = new PasswordField("Current Password");
        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmPassword = new PasswordField("Confirm New Password");

        oldPassword.setWidthFull();
        newPassword.setWidthFull();
        confirmPassword.setWidthFull();

        Button changePassBtn = new Button("Change Password");
        changePassBtn.addClickListener(e -> {
            String oldPass = oldPassword.getValue();
            String newPass = newPassword.getValue();
            String confirm = confirmPassword.getValue();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Notification.show("Please fill all fields").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (newPass.length() < 6) {
                Notification.show("Password must be at least 6 characters").addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (!newPass.equals(confirm)) {
                Notification.show("New passwords do not match").addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                try {
                    // FIXED: Using changePassword(id, old, new)
                    userService.changePassword(currentUser.getId(), oldPass, newPass);

                    Notification.show("Password changed successfully!", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    oldPassword.clear();
                    newPassword.clear();
                    confirmPassword.clear();
                } catch (RuntimeException ex) {
                    Notification.show(ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

        VerticalLayout section = new VerticalLayout(title, oldPassword, newPassword, confirmPassword, changePassBtn);
        section.getStyle().set("border", "1px solid #e0e0e0").set("border-radius", "8px").set("padding", "20px");
        section.setWidthFull();
        return section;
    }

    private VerticalLayout createDangerZone() {
        H4 title = new H4("Danger Zone");
        title.getStyle().set("color", "#d32f2f");

        Span warning = new Span("Deactivating your account will disable your access immediately.");

        Button deactivateBtn = new Button("Deactivate Account");
        deactivateBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        deactivateBtn.addClickListener(e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Deactivate Account?");
            dialog.setText("Are you sure? You will need to contact support to reactivate it.");
            dialog.setCancelable(true);
            dialog.setConfirmText("Deactivate");
            dialog.setConfirmButtonTheme("error primary");

            dialog.addConfirmListener(event -> {
                // FIXED: Using deactivateUser instead of deleteUser
                userService.deactivateUser(currentUser.getId());
                securityService.logout();
            });

            dialog.open();
        });

        VerticalLayout section = new VerticalLayout(title, warning, deactivateBtn);
        section.getStyle().set("border", "1px solid #f8d7da").set("background-color", "#fff5f5").set("border-radius", "8px").set("padding", "20px");
        section.setWidthFull();
        return section;
    }
}