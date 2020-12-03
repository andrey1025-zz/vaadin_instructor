package com.petrz.instructors.views.semesters;

import java.util.Optional;

import com.petrz.instructors.data.entity.Semester;
import com.petrz.instructors.data.service.SemesterService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import com.petrz.instructors.views.main.MainView;

@Route(value = "semesters", layout = MainView.class)
@PageTitle("Semesters")
@CssImport("./styles/views/semesters/semesters-view.css")
public class SemestersView extends Div {

    private Grid<Semester> grid;

    private TextField name = new TextField("Semester name");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Semester> binder;

    private Semester semester = new Semester();

    private SemesterService semesterService;

    public SemestersView(@Autowired SemesterService semesterService) {
        setId("semesters-view");

        this.semesterService = semesterService;
        // Configure Grid
        grid = new Grid<>(Semester.class);
        grid.setColumns("name");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setDataProvider(new CrudServiceDataProvider<Semester, Void>(semesterService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Semester> semesterFromBackend = semesterService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (semesterFromBackend.isPresent()) {
                    populateForm(semesterFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Semester.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.semester == null) {
                    this.semester = new Semester();
                }
                binder.writeBean(this.semester);
                semesterService.update(this.semester);
                clearForm();
                refreshGrid();
                Notification.show("Semester details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the semester details.");
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
        AbstractField[] fields = new AbstractField[] { name };
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
        buttonLayout.add(save, cancel);
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

    private void populateForm(Semester value) {
        this.semester = value;
        binder.readBean(this.semester);
    }
}
