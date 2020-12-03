package com.petrz.instructors.views.sections;

import com.petrz.instructors.data.entity.Course;
import com.petrz.instructors.data.entity.Instructor;
import com.petrz.instructors.data.entity.Section;
import com.petrz.instructors.data.service.CourseService;
import com.petrz.instructors.data.service.InstructorService;
import com.petrz.instructors.data.service.SectionService;
import com.petrz.instructors.views.main.MainView;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

import java.util.List;
import java.util.Optional;

@Route(value = "sections", layout = MainView.class)
@PageTitle("Sections")
@CssImport("./styles/views/sections/sections-view.css")
public class SectionsView extends Div {

    private Grid<Section> grid;

    private Select<Course> course = new Select<>();
    private Select<Instructor> instructor = new Select<>();
    private TextField name = new TextField("Section name");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Section> binder;

    private Section section = new Section();

    private SectionService sectionService;
    private CourseService courseService;
    private InstructorService instructorService;

    public SectionsView(@Autowired SectionService sectionService,@Autowired CourseService courseService,@Autowired InstructorService instructorService) {
        setId("sections-view");

        this.sectionService = sectionService;
        this.courseService = courseService;
        this.instructorService = instructorService;
        // Configure Grid
        //grid = new Grid<>(Section.class);
        grid = new Grid<>(Section.class);
        grid.removeAllColumns();
        grid.addColumn("course.name").setHeader("Course");
        grid.addColumn("instructor.name").setHeader("Instructor");
        grid.addColumn("name").setHeader("Section number");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setDataProvider(new CrudServiceDataProvider<Section, Void>(this.sectionService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Section> sectionFromBackend = this.sectionService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (sectionFromBackend.isPresent()) {
                    populateForm(sectionFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        course.setLabel("Course");
        course.setRequiredIndicatorVisible(true);
        course.setDataProvider(new CrudServiceDataProvider<Course, Void>(this.courseService));
        course.setItemLabelGenerator(Course::getName);

        instructor.setLabel("Instructor");
        instructor.setRequiredIndicatorVisible(true);
        instructor.setDataProvider(new CrudServiceDataProvider<Instructor, Void>(this.instructorService));
        instructor.setItemLabelGenerator(Instructor::getName);

        // Configure Form
        binder = new Binder<>(Section.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.section == null) {
                    this.section = new Section();
                }
                binder.writeBean(this.section);
                this.sectionService.update(this.section);
                clearForm();
                refreshGrid();
                Notification.show("Section details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the section details.");
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
        AbstractField[] fields = new AbstractField[] { course , instructor , name };
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

    private void populateForm(Section value) {
        this.section = value;
        binder.readBean(this.section);
    }
}

