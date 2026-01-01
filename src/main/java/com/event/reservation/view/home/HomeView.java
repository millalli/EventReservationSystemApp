package com.event.reservation.view.home;

import com.event.reservation.layout.MainLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.button.ButtonVariant;
@Route(value = "", layout = MainLayout.class) // Utilise le MainLayout qui contient déjà la Navbar
@PageTitle("Event Gallery")
@AnonymousAllowed
@CssImport(value = "./styles/shared-styles.css")

public class HomeView extends VerticalLayout {

    public HomeView() {
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        setAlignItems(Alignment.CENTER); // Centre tout le contenu

        // On appelle les méthodes qui créent les morceaux de page
        // (Pas besoin de 'new NavbarSection', c'est dans MainLayout)

        add(
                createHeroSection(),       // Le gros bloc image/texte
                createGallerySection(),    // Les photos
                createCategorySection(),   // Les catégories
                createTestimonialSection(),// Les avis
                createCTASection(),
                createFooterSection()      // Le pied de page
        );
    }

    // --- 1. HERO SECTION (L'image sombre et le texte) ---
    private Component createHeroSection() {
        Div hero = new Div();
        hero.addClassName("hero-section"); // Classe CSS définie dans styles.css

        VerticalLayout content = new VerticalLayout();
        content.addClassName("hero-content");
        content.setAlignItems(Alignment.CENTER);
        content.setJustifyContentMode(JustifyContentMode.CENTER);


// 1. Welcome Text
        H3 welcome = new H3("Welcome");
// On ajoute le style de base + l'animation + le délai court
        welcome.addClassNames("hero-welcome", "anim-text", "delay-100");

// 2. Les lignes principales
        H1 line1 = new H1("EXPLORE & BOOK");
        H1 line2 = new H1("UNFORGETTABLE EVENTS");
        H1 line3 = new H1("ALL IN ONE PLACE.");

// On applique l'animation avec des délais croissants pour l'effet "cascade"
        line1.addClassNames("hero-big-text", "anim-text", "delay-200");
        line2.addClassNames("hero-big-text", "anim-text", "delay-300");
        line3.addClassNames("hero-big-text", "anim-text", "delay-400");

// Ensuite vous ajoutez tout ça à votre layout comme d'habitude...
        content.add(welcome, line1, line2, line3);
        hero.add(content);
        return hero;
    }

    // --- 2. GALLERY SECTION ---
// --- 2. GALLERY SECTION ---
// --- 2. GALLERY SECTION ---
// --- 2. GALLERY SECTION ---
    private Component createGallerySection() {
        VerticalLayout section = new VerticalLayout();
        section.setAlignItems(Alignment.CENTER);
        section.setPadding(true);
        section.addClassName("gallery-section");

        // Translated Title
        H2 title = new H2("Our Featured Events");
        title.getStyle().set("font-size", "2.5rem");

        // Translated Description
        Paragraph desc = new Paragraph("Discover the best experiences in the heart of Morocco.");
        desc.getStyle().set("color", "#666");
        desc.getStyle().set("font-size", "1.1rem");

        // Card Container
        Div cardContainer = new Div();
        cardContainer.addClassName("event-card-container");

        // --- ADDING CARDS (English Text + Moroccan Cities) ---
        cardContainer.add(
                // 1. Conference (Business / Tech) - Casablanca
                createEventCard(
                        "Africa Tech Summit",
                        "Casablanca, Morocco",
                        "https://images.unsplash.com/photo-1544531586-fde5298cdd40?auto=format&fit=crop&w=600&q=80"
                ),

                // 2. Music (Festival) - Rabat -> NEW WORKING IMAGE
                createEventCard(
                        "Rhythms of the World",
                        "Rabat, Morocco",
                        "https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=600&q=80"                ),

                // 3. Theater / Opera - Rabat
                createEventCard(
                        "Night at the Theater",
                        "Grand Theatre, Rabat",
                        "https://images.unsplash.com/photo-1507924538820-ede94a04019d?auto=format&fit=crop&w=600&q=80"
                ),

                // 4. Art & Culture - Marrakech
                createEventCard(
                        "Art & Soul Expo",
                        "Marrakech, Morocco",
                        "https://images.unsplash.com/photo-1536924940846-227afb31e2a5?auto=format&fit=crop&w=600&q=80"
                )
        );

        section.add(title, desc, cardContainer);
        return section;
    }
    // Méthode utilitaire pour créer une carte style "BoomPop"
    private Component createEventCard(String company, String location, String imageUrl) {
        Div card = new Div();
        card.addClassName("event-card");

        // On définit l'image de fond dynamiquement
        card.getStyle().set("background-image", "url('" + imageUrl + "')");

        // Contenu texte (Nom de l'entreprise)
        H3 companyName = new H3(company);
        companyName.addClassName("card-company");

        // Contenu lieu (Icone + Texte)
        Span locationText = new Span("📍 " + location); // Vous pouvez remplacer l'emoji par une Icon Vaadin si vous voulez
        locationText.addClassName("card-location");

        // On ajoute tout dans la carte
        card.add(companyName, locationText);
        return card;
    }
// --- 3. CATEGORY SECTION ---
    private Component createCategorySection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("category-section"); // Marge et espacement
        section.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Explore by Category");
        title.getStyle().set("font-size", "2rem");
        title.getStyle().set("margin-bottom", "30px");

        // Conteneur flexible pour les catégories
        Div categoryContainer = new Div();
        categoryContainer.addClassName("category-container");

        // Ajout des catégories avec des icônes Vaadin adaptées
        categoryContainer.add(
                createCategoryItem("CONCERT", VaadinIcon.MUSIC),
                createCategoryItem("THEATRE", VaadinIcon.STAR), // L'étoile pour le spectacle/scène
                createCategoryItem("CONFERENCE", VaadinIcon.PRESENTATION),
                createCategoryItem("SPORT", VaadinIcon.TROPHY),
                createCategoryItem("AUTRE", VaadinIcon.GRID_BIG) // Grille pour "Divers"
        );

        section.add(title, categoryContainer);
        return section;
    }

    // Méthode utilitaire pour créer une petite carte de catégorie
    private Component createCategoryItem(String name, VaadinIcon icon) {
        Div card = new Div();
        card.addClassName("category-card");

        // L'icône
        Icon i = icon.create();
        i.addClassName("category-icon");

        // Le texte
        Span text = new Span(name);
        text.addClassName("category-text");

        card.add(i, text);

        // Rendre la carte cliquable (optionnel, pour future navigation)
        card.addClickListener(e -> {
            System.out.println("Click sur : " + name);
            // UI.getCurrent().navigate(EventsView.class, name); // Exemple de navigation
        });

        return card;
    }

    // --- 4. TESTIMONIALS SECTION ---
    private Component createTestimonialSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("testimonial-section");
        section.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Trusted by Event Planners");
        title.getStyle().set("font-size", "2.5rem");
        title.getStyle().set("margin-bottom", "40px");

        // Conteneur des cartes
        Div container = new Div();
        container.addClassName("testimonial-container");

        // Ajout des témoignages (Données simulées basées sur votre image)
        container.add(
                createTestimonialCard(
                        "Very helpful end-to-end event planning assistance! Quick turnaround and wonderful customer service.",
                        "Rachel Nakauchi",
                        "Executive Assistant",
                        "Instacart"
                ),
                createTestimonialCard(
                        "Working with Event Gallery allowed me to feel excited about this offsite and happily take on planning the next.",
                        "Nicole B.",
                        "VP of People",
                        "Gremlin"
                ),
                createTestimonialCard(
                        "It was a matter of evaluating where we put our resources, and it made a lot of sense to have folks who do this regularly.",
                        "Kelly S-F.",
                        "Chief of Staff",
                        "Automotus"
                )
        );

        section.add(title, container);
        return section;
    }

    // Méthode utilitaire pour créer une carte de témoignage
    private Component createTestimonialCard(String quoteText, String name, String role, String company) {
        Div card = new Div();
        card.addClassName("testimonial-card");

        // 1. Les Étoiles (5 étoiles)
        HorizontalLayout stars = new HorizontalLayout();
        stars.addClassName("star-rating");
        for (int i = 0; i < 5; i++) {
            Icon star = VaadinIcon.STAR.create();
            stars.add(star);
        }

        // 2. La Citation
        Paragraph quote = new Paragraph("“" + quoteText + "”");
        quote.addClassName("testimonial-quote");

        // 3. Infos Utilisateur (Avatar + Texte)
        HorizontalLayout userInfo = new HorizontalLayout();
        userInfo.setAlignItems(Alignment.CENTER);
        userInfo.setSpacing(true);

        // Avatar (Génère automatiquement les initiales si pas d'image)
        Avatar avatar = new Avatar(name);
        avatar.addClassName("testimonial-avatar");

        // Colonne pour Nom et Titre
        VerticalLayout userDetails = new VerticalLayout();
        userDetails.setPadding(false);
        userDetails.setSpacing(false);

        Span userName = new Span(name);
        userName.addClassName("user-name");

        Span userRole = new Span(role);
        userRole.addClassName("user-role");

        userDetails.add(userName, userRole);
        userInfo.add(avatar, userDetails);

        // 4. Logo Entreprise (Simulé en texte stylisé)
        H3 companyLogo = new H3(company);
        companyLogo.addClassName("company-logo-text");

        // Assemblage
        card.add(stars, quote, userInfo, companyLogo);
        return card;
    }

    // --- 5. CTA SECTION ---
    private Component createCTASection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("cta-section");
        section.setAlignItems(Alignment.CENTER);
        section.setJustifyContentMode(JustifyContentMode.CENTER);

        // Titre impactant
        H2 title = new H2("Ready to experience culture differently?");
        title.addClassName("cta-title");

        // Bouton d'action
        Button exploreBtn = new Button("Explore Events");
        exploreBtn.addClassName("cta-button");

        // Icone de flèche pour le dynamisme (Optionnel)
        exploreBtn.setIcon(VaadinIcon.ARROW_RIGHT.create());
        exploreBtn.setIconAfterText(true);

        // Action au clic (Navigation)
        exploreBtn.addClickListener(e -> {
            // Remplacez "events" par votre route réelle ou EventsView.class
            exploreBtn.getUI().ifPresent(ui -> ui.navigate("events"));
            System.out.println("Navigating to events...");
        });

        section.add(title, exploreBtn);
        return section;
    }
    // --- 5. FOOTER ---
    private Component createFooterSection() {
        // Conteneur Principal (Fond sombre)
        VerticalLayout footer = new VerticalLayout();
        footer.addClassName("app-footer");
        footer.setWidthFull();
        footer.setPadding(false);
        footer.setSpacing(false);

        // --- PARTIE HAUTE : Les Colonnes ---
        HorizontalLayout columnsContainer = new HorizontalLayout();
        columnsContainer.addClassName("footer-columns");
        columnsContainer.setWidthFull();

        // Colonne 1 : Brand / Logo
        VerticalLayout brandCol = new VerticalLayout();
        brandCol.addClassName("footer-col");
        H3 brandTitle = new H3("CultureApp"); // Remplacez par votre nom
        brandTitle.addClassName("footer-brand");
        Span brandDesc = new Span("Experience culture differently. Book unique events in seconds.");
        brandDesc.addClassName("footer-text");
        brandCol.add(brandTitle, brandDesc);

        // Colonne 2 : Company (Navigation)
        VerticalLayout navCol = new VerticalLayout();
        navCol.addClassName("footer-col");
        navCol.add(new H4("Company"));
        navCol.add(createFooterLink("About Us", "about"));
        navCol.add(createFooterLink("Contact", "contact"));
        navCol.add(createFooterLink("FAQ", "faq"));

        // Colonne 3 : Legal
        VerticalLayout legalCol = new VerticalLayout();
        legalCol.addClassName("footer-col");
        legalCol.add(new H4("Legal"));
        legalCol.add(createFooterLink("Terms of Service", "terms"));
        legalCol.add(createFooterLink("Privacy Policy", "privacy"));

        // Colonne 4 : Social Media
        VerticalLayout socialCol = new VerticalLayout();
        socialCol.addClassName("footer-col");
        socialCol.add(new H4("Follow Us"));

        HorizontalLayout socialIcons = new HorizontalLayout();
        socialIcons.addClassName("social-icons-container");

        socialIcons.add(createSocialIcon(VaadinIcon.FACEBOOK));
        socialIcons.add(createSocialIcon(VaadinIcon.TWITTER));


        socialCol.add(socialIcons);

        // Ajout des colonnes au conteneur
        columnsContainer.add(brandCol, navCol, legalCol, socialCol);

        // --- PARTIE BASSE : Copyright ---
        Div copyrightDiv = new Div();
        copyrightDiv.addClassName("footer-copyright");
        copyrightDiv.setText("© " + java.time.Year.now() + " CultureApp. All rights reserved.");

        footer.add(columnsContainer, copyrightDiv);

        return footer;
    }

    // Helper pour créer des liens de navigation propres
    private Anchor createFooterLink(String text, String route) {
        // Utilisation de Anchor pour l'exemple, mais RouterLink est mieux si vous avez les classes
        Anchor link = new Anchor(route, text);
        link.addClassName("footer-link");
        return link;
    }

    // Helper pour les icônes sociales
    private Button createSocialIcon(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.addClassName("social-button");
        btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        return btn;
    }}