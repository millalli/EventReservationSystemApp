package com.event.reservation.view;

import com.event.reservation.entity.User;
import com.event.reservation.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | Event Reservation")
@CssImport(value = "./styles/Register-style.css")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();
    private final SecurityService securityService;

    // Inject SecurityService in the constructor
    public LoginView(SecurityService securityService) {
        this.securityService = securityService;

        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // --- Card Setup ---
        VerticalLayout card = new VerticalLayout();
        card.addClassName("login-card");
        card.setSpacing(false);
        card.setPadding(true);
        card.setAlignItems(Alignment.CENTER);

        // --- Header ---
        H1 title = new H1("Welcome Back");
        title.addClassName("register-title");

        Span subtitle = new Span("Please sign in to continue");
        subtitle.addClassName("register-subtitle");

        // --- Login Form ---
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        // --- NEW: Sign Up / Register Link ---
        Span text = new Span("Don't have an account? ");
        text.getStyle().set("color", "#64748b").set("font-size", "0.9rem");

        RouterLink registerLink = new RouterLink("Sign up", RegisterView.class); // Assumes RegisterView exists
        // If RegisterView class is not found, use: new RouterLink("Sign up", "register");
        registerLink.getStyle().set("font-weight", "600").set("color", "#7c3aed"); // Purple color

        VerticalLayout footer = new VerticalLayout(text, registerLink);
        footer.setAlignItems(Alignment.CENTER);
        footer.setSpacing(false);
        footer.setPadding(false);

        // Add components to card
        card.add(title, subtitle, login, footer);
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // 1. Check if user is ALREADY logged in
        User user = securityService.getAuthenticatedUser();

        if (user != null) {
            // User is already logged in, redirect based on Role
            if (user.getRole().isAdmin()) {
                event.forwardTo("admin/dashboard"); // Modify if your admin route is different
            } else if (user.getRole().isOrganizer()) {
                event.forwardTo("organizer/dashboard");
            } else {
                event.forwardTo("dashboard"); // The Client Dashboard we built
            }
            return; // Stop execution here
        }

        // 2. Handle Login Errors (Visual only)
        if(event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}