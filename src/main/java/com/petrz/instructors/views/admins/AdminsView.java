package com.petrz.instructors.views.admins;

import java.util.Optional;

import com.petrz.instructors.data.entity.Admin;
import com.petrz.instructors.data.service.AdminService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import com.petrz.instructors.views.main.MainView;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

@Route(value = "admins", layout = MainView.class)
@PageTitle("Admins")
@CssImport("./styles/views/admins/admins-view.css")
public class AdminsView extends Div {

    private Grid<Admin> grid;

    private TextField name = new TextField("Name");
    private EmailField email = new EmailField("Email");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private Binder<Admin> binder;

    private Admin admin = new Admin();

    private AdminService adminService;

    public AdminsView(@Autowired AdminService adminService) {
        setId("admins-view");
        this.adminService = adminService;
        // Configure Grid
        grid = new Grid<>(Admin.class);
        grid.setColumns("name", "email");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setDataProvider(new CrudServiceDataProvider<Admin, Void>(adminService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Admin> adminFromBackend = adminService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (adminFromBackend.isPresent()) {
                    populateForm(adminFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Admin.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.admin == null) {
                    this.admin = new Admin();
                }
                binder.writeBean(this.admin);
                adminService.update(this.admin);
                clearForm();
                refreshGrid();
                Notification.show("Admin details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the admin details.");
            }
        });

        delete.addClickListener(e->{
            if( this.admin !=null ) {
                adminService.delete(admin.getId());
                Notification.show("Admin (name:"+name.getValue()+" email:"+email.getValue()+" deleted.");
                clearForm();
                refreshGrid();
            }
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        AbstractField[] fields = new AbstractField[] { name, email };
        for (AbstractField field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Admin value) {
        this.admin = value;
        binder.readBean(this.admin);
    }
}
