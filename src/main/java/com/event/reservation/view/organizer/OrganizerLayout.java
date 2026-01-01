package com.event.reservation.view.organizer;

import com.event.reservation.entity.User;
import com.event.reservation.security.SecurityService;
import com.event.reservation.view.organizer.EventFormView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;

// Ensure this matches your CSS file location
@CssImport("./styles/OrganizerLayout-style.css")
public class OrganizerLayout extends AppLayout {

    private final SecurityService securityService;

    public OrganizerLayout(SecurityService securityService) {
        this.securityService = securityService;
        addClassName("organizer-layout");

        createFloatingHeader();
    }

    private void createFloatingHeader() {
        // 1. The Container
        HorizontalLayout container = new HorizontalLayout();
        container.addClassName("centered-navbar-container");
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        container.setWidthFull();

        // 2. Logo
        H1 logo = new H1("Event Gallery");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Right.LARGE);
        logo.getStyle().set("color", "white");

        // 3. Navigation Links
        RouterLink dashboardLink = createNavLink("Dashboard", OrganizerDashboardView.class, VaadinIcon.DASHBOARD);
        RouterLink eventsLink = createNavLink("My Events", MyEventsView.class, VaadinIcon.CALENDAR_USER);
        RouterLink createLink = createNavLink("Create Event", EventFormView.class, VaadinIcon.PLUS_CIRCLE);

        HorizontalLayout navLinks = new HorizontalLayout(dashboardLink, eventsLink, createLink);
        navLinks.setSpacing(true);

        // 4. Right Side (Toggle + User + Logout)
        User user = securityService.getAuthenticatedUser();
        Span userBadge = new Span(user != null ? user.getPrenom() : "Organizer");
        userBadge.addClassNames(LumoUtility.FontWeight.BOLD);
        userBadge.getStyle().set("color", "white");

        Button logoutBtn = new Button("Log out", new Icon(VaadinIcon.SIGN_OUT), e -> securityService.logout());
        logoutBtn.addClassName("nav-button-primary");

        // --- NEW: Theme Toggle ---
        Button themeToggle = createThemeToggle();
        // Force white color to match the Organizer dark header style
        themeToggle.getStyle().set("color", "white");

        HorizontalLayout rightSection = new HorizontalLayout(themeToggle, userBadge, logoutBtn);
        rightSection.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        rightSection.setSpacing(true);
        rightSection.getStyle().set("margin-left", "auto");

        // 5. Assemble
        container.add(logo, navLinks, rightSection);

        addToNavbar(container);
    }

    private RouterLink createNavLink(String text, Class<? extends com.vaadin.flow.component.Component> viewClass, VaadinIcon icon) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.addClassName("nav-link");

        Icon i = icon.create();
        i.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.Margin.Right.SMALL);

        Span label = new Span(text);

        link.add(i, label);
        return link;
    }

    /**
     * Creates a toggle button for Dark/Light mode with local storage persistence.
     */
    private Button createThemeToggle() {
        Button toggleButton = new Button(new Icon(VaadinIcon.MOON_O));
        toggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        toggleButton.setTooltipText("Toggle Dark Mode");

        // 1. Check Local Storage on Load
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

        // 2. Click Listener
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