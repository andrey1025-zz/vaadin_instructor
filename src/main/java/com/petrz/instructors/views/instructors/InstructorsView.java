package com.petrz.instructors.views.instructors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import com.petrz.instructors.data.entity.Instructor;
import com.petrz.instructors.data.service.InstructorService;
import com.petrz.instructors.util.DownloadLink;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import com.petrz.instructors.views.main.MainView;

@Route(value = "instructors", layout = MainView.class)
@PageTitle("Instructors")
@CssImport("./styles/views/instructors/instructors-view.css")
public class InstructorsView extends Div {

    private Grid<Instructor> grid;

    private TextField customId = new TextField("ID");
    private TextField name = new TextField("Name");
    private TextField email = new TextField("Email");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");


    private Binder<Instructor> binder;

    private Instructor instructor = new Instructor();

    private InstructorService instructorService;

    public InstructorsView(@Autowired InstructorService instructorService) {
        setId("instructors-view");
        this.instructorService = instructorService;
        // Configure Grid
        grid = new Grid<>(Instructor.class);
        grid.setColumns("customId","name", "email");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setDataProvider(new CrudServiceDataProvider<Instructor, Void>(instructorService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Instructor> instructorFromBackend = instructorService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (instructorFromBackend.isPresent()) {
                    populateForm(instructorFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Instructor.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.instructor == null) {
                    this.instructor = new Instructor();
                }
                binder.writeBean(this.instructor);
                instructorService.update(this.instructor);
                clearForm();
                refreshGrid();
                Notification.show("Instructor details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the instructor details.");
            }
        });

        delete.addClickListener(e->{
            if( this.instructor !=null ) {
                instructorService.delete(instructor.getId());
                Notification.show("Instructor (name:"+name.getValue()+" email:"+email.getValue()+" deleted.");
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
        AbstractField[] fields = new AbstractField[] { customId, name, email };
        for (AbstractField field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        createImportLayout(editorLayoutDiv);
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

    private void createImportLayout(Div editorLayoutDiv) {
        HorizontalLayout importLayout = new HorizontalLayout();
        importLayout.setId("import-layout");
        importLayout.setWidthFull();
        importLayout.setSpacing(true);

        DownloadLink importDownload = new DownloadLink("excel/Data_for_the_admins.xlsx", "Data_for_the_admins.xlsx", "Download xls");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload importUpload = new Upload(buffer);
        importUpload.setDropAllowed(false);
        importUpload.setAcceptedFileTypes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        importUpload.addSucceededListener(event -> {
            Notification.show("File uploaded:"/*+event.getMIMEType()+" "*/+event.getFileName()+" "+buffer.getFileData().getFileName());
            importExcelData(buffer);
        });

        importLayout.add(importDownload, importUpload);
        editorLayoutDiv.add(importLayout);
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

    private void populateForm(Instructor value) {
        this.instructor = value;
        binder.readBean(this.instructor);
    }

    private void importExcelData(MemoryBuffer buffer) {
        try {
            Workbook workbook = new XSSFWorkbook(buffer.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int dataNum = 0;
            int rowIdx = 0;
            for (Row row : sheet) {
                if( rowIdx >  0 ) {
                    Instructor instructor = new Instructor();
                    int colIdx = 0;
                    for (Cell cell : row) {
                            switch (colIdx) {
                                case 0:
                                    try {
                                        instructor.setCustomId("" + Double.valueOf(cell.getNumericCellValue()).intValue());
                                    } catch(Exception e) {
                                        instructor.setCustomId(cell.getStringCellValue());
                                    }
                                    break;
                                case 1:
                                    try {
                                        instructor.setName(cell.getStringCellValue());
                                    } catch(Exception e) {
                                        instructor.setName("" + Double.valueOf(cell.getNumericCellValue()).intValue());
                                    }
                                    break;
                                case 2:
                                    instructor.setEmail(cell.getStringCellValue());
                                    break;

                            }
                            colIdx++;
                    }
                    if( instructor.getName()!=null || instructor.getEmail()!=null ) {
                        if( instructor.getCustomId()==null ) {
                            instructor.setCustomId("");
                        }
                        if( instructor.getName()==null ) {
                            instructor.setName("");
                        }
                        if( instructor.getEmail()==null ) {
                            instructor.setEmail("");
                        }
                        instructorService.update(instructor);
                        dataNum++;
                    }
                }
                rowIdx++;
            }
            refreshGrid();
            Notification.show("Import finished. Imported #:"+dataNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
