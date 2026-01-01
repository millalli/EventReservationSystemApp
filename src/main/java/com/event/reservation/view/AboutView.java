package com.event.reservation.view;

import com.event.reservation.layout.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("About Us | Event Gallery")
@CssImport(value = "./styles/about-style.css")
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        addClassName("about-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        Div content = new Div();
        content.addClassName("about-content");

        // 1. Hero Section (Renommé pour éviter le conflit avec Home)
        content.add(createHeroSection());

        // 2. What We Actually Do
        content.add(createWhatWeDoSection());

        // 3. Our Mission
        content.add(createMissionSection());

        // 4. The Platform
        content.add(createPlatformSection());

        // 5. Footer (Remplace le "Get Started")
        content.add(createFooter());

        add(content);
    }

    private Div createHeroSection() {
        Div hero = new Div();
        // Nom de classe unique pour ne pas toucher la Home Page
        hero.addClassName("about-hero-section");

        Div overlay = new Div();
        overlay.addClassName("about-hero-overlay");

        Div textContainer = new Div();
        textContainer.addClassName("about-hero-text");

        Span label = new Span("ABOUT US");
        label.addClassName("about-label-hero");

        H1 title = new H1("We Turn Chaos\nInto Chemistry");
        title.addClassName("about-hero-title");

        Span subtitle = new Span("Discovering and booking the best events shouldn't be a hassle. We made it an experience.");
        subtitle.addClassName("about-hero-subtitle");

        textContainer.add(label, title, subtitle);
        overlay.add(textContainer);
        hero.add(overlay);

        return hero;
    }

    private Div createWhatWeDoSection() {
        Div section = new Div();
        section.addClassName("about-section");

        Div header = new Div();
        header.addClassName("about-section-header");
        Span label = new Span("WHAT WE ACTUALLY DO");
        label.addClassName("about-section-label");
        header.add(label);

        H2 mainText = new H2("Event Gallery is an AI-powered event planning platform. It makes good times easy to pull off, and makes annoying tasks simple.");
        mainText.addClassName("about-big-statement");

        H3 subText = new H3("Our users love it.\nBecause here, tech isn't lifeless.");
        subText.addClassName("about-sub-statement");

        section.add(header, mainText, subText);
        return section;
    }

    private Div createMissionSection() {
        Div section = new Div();
        section.addClassName("about-section");

        Div header = new Div();
        header.addClassName("about-section-header");
        Span label = new Span("OUR MISSION");
        label.addClassName("about-section-label");
        header.add(label);

        Div grid = new Div();
        grid.addClassName("about-mission-grid");

        Image img = new Image("https://images.unsplash.com/photo-1529156069898-49953e39b3ac?q=80&w=1000&auto=format&fit=crop", "Mission mood");
        img.addClassName("about-mission-image");

        Div textCol = new Div();
        textCol.addClassName("about-mission-text");

        textCol.add(new H3("Corporate. Ugh."));
        textCol.add(new Paragraph("A collective yawn could swallow us whole at the sight of the word."));
        textCol.add(new Paragraph("We're here to craft sensory-rich experiences. To make work functions, less work. To celebrate the best parts about being human. It's our obligation to the universe."));

        Span highlight = new Span("Event Gallery creates culture through real, in-person chemistry.");
        highlight.addClassName("about-mission-highlight");
        textCol.add(highlight);

        grid.add(img, textCol);
        section.add(header, grid);

        return section;
    }

    private Div createPlatformSection() {
        Div section = new Div();
        section.addClassName("about-section");

        Div header = new Div();
        header.addClassName("about-section-header");
        Span label = new Span("THE PLATFORM");
        label.addClassName("about-section-label");
        header.add(label);

        Div featuresGrid = new Div();
        featuresGrid.addClassName("about-features-grid");

        featuresGrid.add(createFeatureItem(VaadinIcon.SEARCH, "Easy to Explore", "Find what moves you in seconds with our smart filters."));
        featuresGrid.add(createFeatureItem(VaadinIcon.TICKET, "Instant Booking", "Secure your spot without the paperwork. One click, you're in."));
        featuresGrid.add(createFeatureItem(VaadinIcon.GROUP, "Community Driven", "See who's going, share with friends, and build your network."));

        section.add(header, featuresGrid);
        return section;
    }

    private Div createFeatureItem(VaadinIcon icon, String title, String desc) {
        Div item = new Div();
        item.addClassName("about-feature-item");

        Icon i = icon.create();
        i.addClassName("about-feature-icon");

        H4 h = new H4(title);
        Paragraph p = new Paragraph(desc);

        item.add(i, h, p);
        return item;
    }

    // --- NOUVEAU FOOTER ---
    private Div createFooter() {
        Div footer = new Div();
        footer.addClassName("about-footer");

        Div content = new Div();
        content.addClassName("about-footer-content");

        // Copyright
        Span copyright = new Span("© 2024 Event Gallery Inc.");
        copyright.addClassName("footer-copy");

        // Liens
        HorizontalLayout links = new HorizontalLayout();
        links.addClassName("footer-links");

        Anchor terms = new Anchor("#", "Terms");
        Anchor privacy = new Anchor("#", "Privacy");
        Anchor contact = new Anchor("#", "Contact");

        links.add(terms, privacy, contact);

        content.add(copyright, links);
        footer.add(content);

        return footer;
    }
}