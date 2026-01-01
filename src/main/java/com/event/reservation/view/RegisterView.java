package com.event.reservation.view;

import com.event.reservation.dto.RegisterRequest;
import com.event.reservation.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@PageTitle("Register | Event System")
@AnonymousAllowed
@CssImport(value = "./styles/Register-style.css")

public class RegisterView extends VerticalLayout {

    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;

        // 1. Config de la page entière (le fond d'écran)
        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // 2. Création de la "Carte" blanche au centre
        VerticalLayout card = new VerticalLayout();
        card.addClassName("register-card");
        card.setSpacing(true);
        card.setPadding(true);

        // Titre
        H1 title = new H1("Create Account");
        title.addClassName("register-title");
        Span subtitle = new Span("Join our community today");
        subtitle.addClassName("register-subtitle");

        // Champs
        TextField nom = new TextField("Nom");
        TextField prenom = new TextField("Prénom");
        TextField telephone = new TextField("Téléphone");
        EmailField email = new EmailField("Email");
        PasswordField password = new PasswordField("Password");

        // Pour que les champs prennent toute la largeur de la carte
        nom.setWidthFull();
        prenom.setWidthFull();
        telephone.setWidthFull();
        email.setWidthFull();
        password.setWidthFull();

        // Bouton
        Button registerBtn = new Button("Sign Up", e -> {
            try {
                RegisterRequest request = new RegisterRequest(
                        nom.getValue(),
                        prenom.getValue(),
                        telephone.getValue(),
                        email.getValue(),
                        password.getValue()
                );
                authService.register(request);
                Notification.show("Registration successful!");
                getUI().ifPresent(ui -> ui.navigate("login"));
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });
        registerBtn.addClassName("register-button");
        registerBtn.setWidthFull();

        // FormLayout pour organiser les champs (1 colonne pour être propre)
        FormLayout form = new FormLayout();
        form.add(nom, prenom, telephone, email, password);
        // Force 1 seule colonne pour que ce soit vertical et propre
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        // 3. On ajoute tout DANS la carte, puis la carte DANS la vue
        card.add(title, subtitle, form, registerBtn);
        add(card);
    }}
