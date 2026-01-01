package com.event.reservation.view.organizer;

import com.event.reservation.entity.Event;
import com.event.reservation.entity.EventCategory;
import com.event.reservation.entity.EventStatus;
import com.event.reservation.view.organizer.OrganizerLayout;
import com.event.reservation.security.SecurityService;
import com.event.reservation.service.EventService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Route(value = "organizer/events/edit", layout = OrganizerLayout.class)
@RouteAlias(value = "organizer/events/new", layout = OrganizerLayout.class)
@PageTitle("Manage Event | Event Gallery")
@RolesAllowed({"ORGANIZER", "ADMIN"})
public class EventFormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final EventService eventService;
    private final SecurityService securityService;

    private Event event;
    private final Binder<Event> binder = new BeanValidationBinder<>(Event.class);

    // --- FORM FIELDS ---
    private TextField titre = new TextField("Event Title");
    private ComboBox<EventCategory> categorie = new ComboBox<>("Category");
    private DateTimePicker dateDebut = new DateTimePicker("Start Date");
    private DateTimePicker dateFin = new DateTimePicker("End Date");

    // Added 'Ville' matching Entity @NotBlank
    private TextField ville = new TextField("City");
    // Added 'Lieu' matching Entity @NotBlank
    private TextField lieu = new TextField("Address / Venue");

    // Added 'PrixUnitaire' matching Entity @NotNull
    private NumberField prixUnitaire = new NumberField("Price (€)");

    private IntegerField capaciteMax = new IntegerField("Max Capacity");
    private TextArea description = new TextArea("Description");
    private TextField imageUrl = new TextField("Image URL (Optional)");

    // --- PREVIEW COMPONENTS ---
    private Div previewCard;
    private Image previewImage;
    private H3 previewTitle;
    private Span previewCategory;
    private Span previewDate;
    private Span previewLocation;
    private Span previewPrice; // Added price to preview
    private Paragraph previewDesc;

    public EventFormView(EventService eventService, SecurityService securityService) {
        this.eventService = eventService;
        this.securityService = securityService;

        addClassName("event-form-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // 1. Setup UI
        configureFormFields();
        configureBinder();

        // 2. Build Split Layout
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(createFormSection());
        splitLayout.addToSecondary(createPreviewSection());
        splitLayout.setSplitterPosition(60); // 60% Form, 40% Preview

        add(splitLayout);
    }

    // --- ROUTER & LOAD DATA ---
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long eventId) {
        if (eventId != null) {
            // EDIT MODE
            this.event = eventService.findById(eventId).orElse(new Event());
        } else {
            // NEW MODE
            this.event = new Event();
            // We set default values later in save() or here, but Binder needs an object now
        }

        binder.readBean(this.event);
        updatePreview();
    }

    // --- CONFIGURATION ---
    private void configureFormFields() {
        categorie.setItems(EventCategory.values());

        // Price Config
        prixUnitaire.setMin(0);
        prixUnitaire.setSuffixComponent(new Span("€"));
        prixUnitaire.setValueChangeMode(ValueChangeMode.EAGER);
        prixUnitaire.addValueChangeListener(e -> updatePreview());

        // Date Logic
        dateDebut.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                dateFin.setMin(e.getValue());
            }
            updatePreview();
        });
        dateFin.addValueChangeListener(e -> updatePreview());

        // Capacity
        capaciteMax.setMin(1);
        capaciteMax.setStepButtonsVisible(true);

        description.setHeight("150px");

        // Live Preview Triggers
        titre.setValueChangeMode(ValueChangeMode.EAGER);
        titre.addValueChangeListener(e -> updatePreview());

        categorie.addValueChangeListener(e -> updatePreview());

        ville.setValueChangeMode(ValueChangeMode.EAGER);
        ville.addValueChangeListener(e -> updatePreview());

        imageUrl.setValueChangeMode(ValueChangeMode.LAZY);
        imageUrl.addValueChangeListener(e -> updatePreview());
    }

    private void configureBinder() {
        // Explicit Bindings to match Entity Getters/Setters exactly

        binder.forField(titre).asRequired("Title is required")
                .bind(Event::getTitre, Event::setTitre);

        binder.forField(categorie).asRequired("Category is required")
                .bind(Event::getCategorie, Event::setCategorie);

        binder.forField(dateDebut).asRequired("Start date required")
                .bind(Event::getDateDebut, Event::setDateDebut);

        binder.forField(dateFin).asRequired("End date required")
                .bind(Event::getDateFin, Event::setDateFin);

        binder.forField(capaciteMax).asRequired("Capacity required")
                .bind(Event::getCapaciteMax, Event::setCapaciteMax);

        // --- NEW FIELDS BINDING ---
        binder.forField(ville).asRequired("City is required")
                .bind(Event::getVille, Event::setVille);

        binder.forField(lieu).asRequired("Address is required")
                .bind(Event::getLieu, Event::setLieu);

        binder.forField(prixUnitaire).asRequired("Price is required")
                .bind(Event::getPrixUnitaire, Event::setPrixUnitaire);

        // Simple Bindings (names match)
        binder.bind(description, "description");
        binder.bind(imageUrl, "imageUrl");
    }

    private Component createFormSection() {
        FormLayout formLayout = new FormLayout();

        // Add all fields to the form
        formLayout.add(
                titre,
                categorie,
                dateDebut, dateFin,
                ville, lieu,        // City & Address
                prixUnitaire,       // Price
                capaciteMax,
                imageUrl,
                description
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        formLayout.setColspan(titre, 2);
        formLayout.setColspan(description, 2);
        formLayout.setColspan(imageUrl, 2);

        // Buttons
        Button saveDraftBtn = new Button("Save Draft", e -> saveEvent(EventStatus.BROUILLON));
        saveDraftBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button publishBtn = new Button("Publish Event", e -> saveEvent(EventStatus.PUBLIE));
        publishBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> UI.getCurrent().navigate(MyEventsView.class));

        HorizontalLayout buttons = new HorizontalLayout(publishBtn, saveDraftBtn, cancelBtn);
        buttons.setPadding(true);

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.add(new H2(event != null && event.getId() != null ? "Edit Event" : "New Event"));
        wrapper.add(formLayout, buttons);
        wrapper.setPadding(true);
        return wrapper;
    }

    // --- PREVIEW SECTION ---
    private Component createPreviewSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSizeFull();

        layout.add(new H3("Live Preview"));

        previewCard = new Div();
        previewCard.addClassName("event-card");
        previewCard.setWidth("350px");
        previewCard.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        previewImage = new Image();
        previewImage.setWidth("100%");
        previewImage.setHeight("200px");
        previewImage.getStyle().set("object-fit", "cover");
        previewImage.setSrc("https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=500&q=60");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(true);

        previewCategory = new Span("CATEGORY");
        previewCategory.getStyle().set("color", "var(--lumo-primary-text-color)").set("font-size", "0.8rem").set("font-weight", "bold").set("text-transform", "uppercase");

        previewPrice = new Span("0.0 €");
        previewPrice.getStyle().set("background", "#e0f2f1").set("color", "#00695c").set("padding", "4px 8px").set("border-radius", "4px").set("font-weight", "bold").set("float", "right");

        HorizontalLayout topRow = new HorizontalLayout(previewCategory, previewPrice);
        topRow.setWidthFull();
        topRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        previewTitle = new H3("Event Title");
        previewTitle.getStyle().set("margin", "0.5em 0");

        previewDate = new Span("Date TBD");
        previewDate.addComponentAsFirst(new Icon(VaadinIcon.CALENDAR));
        previewDate.getStyle().set("font-size", "0.9rem").set("color", "var(--lumo-secondary-text-color)").set("display", "flex").set("align-items", "center").set("gap", "5px");

        previewLocation = new Span("Location");
        previewLocation.addComponentAsFirst(new Icon(VaadinIcon.MAP_MARKER));
        previewLocation.getStyle().set("font-size", "0.9rem").set("color", "var(--lumo-secondary-text-color)").set("display", "flex").set("align-items", "center").set("gap", "5px");

        previewDesc = new Paragraph("Description...");
        previewDesc.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size", "0.9em").set("display", "-webkit-box").set("-webkit-line-clamp", "3").set("-webkit-box-orient", "vertical").set("overflow", "hidden");

        content.add(topRow, previewTitle, previewDate, previewLocation, previewDesc);
        previewCard.add(previewImage, content);

        layout.add(previewCard);
        return layout;
    }

    private void updatePreview() {
        previewTitle.setText(titre.getValue().isEmpty() ? "Event Title" : titre.getValue());
        previewCategory.setText(categorie.getValue() != null ? categorie.getValue().name() : "CATEGORY");

        String cityVal = ville.getValue().isEmpty() ? "City" : ville.getValue();
        previewLocation.setText(cityVal);

        Double priceVal = prixUnitaire.getValue();
        previewPrice.setText(priceVal != null ? priceVal + " €" : "0.0 €");

        if (dateDebut.getValue() != null) {
            previewDate.setText(dateDebut.getValue().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        }

        String desc = description.getValue();
        previewDesc.setText(desc.isEmpty() ? "Description..." : desc);

        if (!imageUrl.getValue().isEmpty()) {
            previewImage.setSrc(imageUrl.getValue());
        }
    }

    // --- SAVE LOGIC ---
    private void saveEvent(EventStatus targetStatus) {
        try {
            // 1. Write form data to Bean
            binder.writeBean(event);

            // 2. Set Status
            event.setStatut(targetStatus);

            // 3. Handle New vs Edit Metadata
            if (event.getId() == null) {
                // It's a NEW event
                event.setOrganisateur(securityService.getAuthenticatedUser());
                event.setDateCreation(LocalDateTime.now()); // Fixes potential null error
            }
            event.setDateModification(LocalDateTime.now());

            // 4. Save
            eventService.save(event);

            Notification.show("Event saved successfully!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            UI.getCurrent().navigate(MyEventsView.class);

        } catch (ValidationException e) {
            Notification.show("Please check the form for errors.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            e.printStackTrace(); // Log error to console
            Notification.show("Error saving event: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}