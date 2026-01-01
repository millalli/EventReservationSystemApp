package com.event.reservation.view.admin;

import com.event.reservation.entity.User;
import com.event.reservation.security.SecurityService;
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

@CssImport("./styles/OrganizerLayout-style.css") // Reuse the same CSS!
public class AdminLayout extends AppLayout {

    private final SecurityService securityService;

    public AdminLayout(SecurityService securityService) {
        this.securityService = securityService;
        addClassName("organizer-layout"); // Reuse class for background/style
        createFloatingHeader();
    }

    private void createFloatingHeader() {
        HorizontalLayout container = new HorizontalLayout();
        container.addClassName("centered-navbar-container");
        container.setWidthFull();
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // 1. Logo
        H1 logo = new H1("Admin Portal");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.EXTRABOLD);
        logo.getStyle().set("color", "white");
        logo.getStyle().set("margin", "0");

        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.getStyle().set("margin-right", "40px");

        // 2. Navigation Links
        RouterLink dashboardLink = createNavLink("Overview", AdminDashboardView.class, VaadinIcon.DASHBOARD);
        RouterLink usersLink = createNavLink("Users", UserManagementView.class, VaadinIcon.USERS);
        RouterLink eventsLink = createNavLink("Events", AllEventsManagementView.class, VaadinIcon.CALENDAR);
        RouterLink reservationsLink = createNavLink("Reservations", AllReservationsView.class, VaadinIcon.TICKET);

        HorizontalLayout navLinks = new HorizontalLayout(dashboardLink, usersLink, eventsLink, reservationsLink);
        navLinks.setSpacing(true);
        navLinks.setAlignItems(FlexComponent.Alignment.CENTER);

        // 3. User Info & Toggle (Right Section)
        User user = securityService.getAuthenticatedUser();
        Span userBadge = new Span("Administrator");
        userBadge.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);
        userBadge.getStyle().set("color", "#e0e0e0");

        Button logoutBtn = new Button("Log out", new Icon(VaadinIcon.SIGN_OUT), e -> securityService.logout());
        logoutBtn.addClassName("nav-button-primary");

        // --- NEW: Theme Toggle ---
        Button themeToggle = createThemeToggle();
        // Force white color to match the Admin dark header style
        themeToggle.getStyle().set("color", "white");

        HorizontalLayout rightSection = new HorizontalLayout(themeToggle, userBadge, logoutBtn);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSection.setSpacing(true);

        // Assemble
        container.add(logoWrapper, navLinks, rightSection);
        addToNavbar(container);
    }

    private RouterLink createNavLink(String text, Class<? extends com.vaadin.flow.component.Component> viewClass, VaadinIcon icon) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.addClassName("nav-link");
        Icon i = icon.create();
        i.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.Margin.Right.SMALL);
        link.add(i, new Span(text));
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