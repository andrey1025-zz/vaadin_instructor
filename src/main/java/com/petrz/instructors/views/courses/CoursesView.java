package com.petrz.instructors.views.courses;

import java.util.Optional;

import com.petrz.instructors.data.entity.Course;
import com.petrz.instructors.data.service.CourseService;
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

@Route(value = "courses", layout = MainView.class)
@PageTitle("Courses")
@CssImport("./styles/views/courses/courses-view.css")
public class CoursesView extends Div {

    private Grid<Course> grid;
    private TextField name = new TextField("Course name");
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Course> binder;
    private Course course = new Course();
    private CourseService courseService;

    public CoursesView(@Autowired CourseService courseService) {
        setId("courses-view");
        this.courseService = courseService;
        // Configure Grid
        grid = new Grid<>(Course.class);
        grid.setColumns("name");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setDataProvider(new CrudServiceDataProvider<Course, Void>(courseService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Course> courseFromBackend = courseService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (courseFromBackend.isPresent()) {
                    populateForm(courseFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Course.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {

            if( name.getValue().trim().equals("") ) {
                Notification.show("Name cannot be empty");
                return;
            }

            try {
                if (this.course == null) {
                    this.course = new Course();
                }
                binder.writeBean(this.course);
                courseService.update(this.course);
                clearForm();
                refreshGrid();
                Notification.show("Course details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the course details.");
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

        name.setRequired(true);
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

    private void populateForm(Course value) {
        this.course = value;
        binder.readBean(this.course);
    }
}
