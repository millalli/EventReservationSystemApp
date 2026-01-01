package com.event.reservation.layout;

import com.event.reservation.entity.User;
import com.event.reservation.security.SecurityService;
import com.event.reservation.view.AboutView;
import com.event.reservation.view.event.EventsView;
import com.event.reservation.view.home.HomeView;
import com.event.reservation.view.client.MyReservationsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;

@AnonymousAllowed
@CssImport(value = "./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    // Inject SecurityService
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;

        // 1. Check who is logged in
        User currentUser = securityService.getAuthenticatedUser();

        // ----------------------------------------------------------------------------------
        // SECTION 1: LOGO (Left Side)
        // ----------------------------------------------------------------------------------
        Image logoImg = new Image("images/favicon.ico.png", "Logo");
        logoImg.setHeight("40px");
        logoImg.addClassName("logo-image");

        H1 logo = new H1("Event Gallery");
        logo.getStyle().set("font-size", "22px");
        logo.getStyle().set("margin", "0");

        HorizontalLayout logoSection = new HorizontalLayout(logo);
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);

        // ----------------------------------------------------------------------------------
        // SECTION 2: NAV LINKS (Center)
        // ----------------------------------------------------------------------------------
        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        RouterLink eventsLink = new RouterLink("Events", EventsView.class);
        RouterLink aboutLink = new RouterLink("About Us", AboutView.class);

        homeLink.addClassName("nav-link");
        eventsLink.addClassName("nav-link");
        aboutLink.addClassName("nav-link");

        HorizontalLayout centerLinks = new HorizontalLayout(homeLink, eventsLink, aboutLink);
        centerLinks.setSpacing(true);
        centerLinks.getStyle().set("gap", "10px");
        centerLinks.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        centerLinks.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // ----------------------------------------------------------------------------------
        // SECTION 3: RIGHT SIDE (Dynamic + Theme Toggle)
        // ----------------------------------------------------------------------------------
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setSpacing(true);
        rightSection.getStyle().set("gap", "12px");
        rightSection.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // --- NEW: Theme Toggle Button ---
        Button themeToggle = createThemeToggle();
        rightSection.add(themeToggle); // Add toggle first

        if (currentUser != null) {
            // === LOGGED IN USER VIEW ===
            RouterLink myResLink = new RouterLink("My Reservations", MyReservationsView.class);
            myResLink.addClassName("nav-link");
            myResLink.getStyle().set("font-weight", "bold");

            // User Name Badge (Clickable -> Goes to Dashboard)
            Button userBadge = new Button(currentUser.getPrenom(), new Icon(VaadinIcon.USER));
            userBadge.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            userBadge.getStyle().set("color", "var(--lumo-body-text-color)"); // Adapt to theme
            userBadge.getStyle().set("cursor", "pointer");

            userBadge.addClickListener(e ->
                    userBadge.getUI().ifPresent(ui -> ui.navigate("dashboard"))
            );

            Button logoutBtn = new Button("Logout", e -> securityService.logout());
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            logoutBtn.getStyle().set("cursor", "pointer");

            rightSection.add(myResLink, userBadge, logoutBtn);

        } else {
            // === GUEST VIEW ===
            Anchor login = new Anchor("/login", "Login");
            Anchor signup = new Anchor("/register", "Sign Up");

            login.addClassName("nav-link");
            signup.addClassNames("nav-link", "nav-button-primary");

            rightSection.add(login, signup);
        }

        // ----------------------------------------------------------------------------------
        // CONTAINER ASSEMBLY
        // ----------------------------------------------------------------------------------
        HorizontalLayout navbarContent = new HorizontalLayout();
        navbarContent.setWidthFull();
        navbarContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navbarContent.getStyle().set("gap", "20px");

        navbarContent.add(logoSection);  // Left
        navbarContent.add(centerLinks);  // Center
        navbarContent.add(rightSection); // Right

        // Keep Center Links Centered
        navbarContent.setFlexGrow(0, logoSection);
        navbarContent.setFlexGrow(1, centerLinks);
        navbarContent.setFlexGrow(0, rightSection);

        centerLinks.getStyle().set("flex-grow", "1");

        // Wrapper
        HorizontalLayout centeredWrapper = new HorizontalLayout(navbarContent);
        centeredWrapper.addClassName("centered-navbar-container");
        centeredWrapper.setMaxWidth("1200px");
        centeredWrapper.getStyle().set("margin", "auto");
        centeredWrapper.getStyle().set("padding", "5px");

        addToNavbar(centeredWrapper);
    }

    /**
     * Creates a toggle button for Dark/Light mode with local storage persistence.
     */
    private Button createThemeToggle() {
        Button toggleButton = new Button(new Icon(VaadinIcon.MOON_O));
        toggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        toggleButton.setTooltipText("Toggle Dark Mode");

        // 1. Check Local Storage on Load to set initial state
        // We use executeJs to get the value from the browser
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('theme')")
                .then(String.class, theme -> {
                    if ("dark".equals(theme)) {
                        UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
                        toggleButton.setIcon(new Icon(VaadinIcon.SUN_O));
                    } else {
                        UI.getCurrent().getElement().getThemeList().remove(Lumo.DARK);
                        toggleButton.setIcon(new Icon(VaadinIcon.MOON_O));
                    }
                });

        // 2. Click Listener to swap themes
        toggleButton.addClickListener(click -> {
            var themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                // Switch to Light
                themeList.remove(Lumo.DARK);
                toggleButton.setIcon(new Icon(VaadinIcon.MOON_O));
                UI.getCurrent().getPage().executeJs("localStorage.removeItem('theme')");
            } else {
                // Switch to Dark
                themeList.add(Lumo.DARK);
                toggleButton.setIcon(new Icon(VaadinIcon.SUN_O));
                UI.getCurrent().getPage().executeJs("localStorage.setItem('theme', 'dark')");
            }
        });

        return toggleButton;
    }
}