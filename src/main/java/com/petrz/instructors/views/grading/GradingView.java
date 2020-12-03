package com.petrz.instructors.views.grading;


import com.petrz.instructors.data.entity.*;
import com.petrz.instructors.data.service.SectionService;
import com.petrz.instructors.data.service.SemesterService;
import com.petrz.instructors.data.service.StudentGradeListService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.petrz.instructors.views.main.MainView;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Route(value = "grading", layout = MainView.class)
@PageTitle("Grading")
@CssImport("styles/views/grading/grading-view.css")
public class GradingView extends Div {

    private SectionService sectionService;
    //private SemesterService semesterService;
    private StudentGradeListService studentGradeListService;
    private Select<Course> course = new Select<>();
    private Select<Section> section = new Select<>();
    //private Select<Semester> semester = new Select<>();
    private Button generateExcelButton = new Button("Download excel");
    private Anchor generateExcelAnchor = new Anchor();
    private Button importStudentGrades = new Button("Import student grades");
    private Integer instructorId;


    public GradingView(@Autowired SectionService sectionService, /*@Autowired SemesterService semesterService,*/
                       @Autowired StudentGradeListService studentGradeListService) {
        setId("grading-view");
        this.sectionService = sectionService;
        //this.semesterService = semesterService;
        this.studentGradeListService = studentGradeListService;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
    }

    private Component createTitle() {
        return new H3("Grading ");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();

        course.setLabel("Course");
        course.setRequiredIndicatorVisible(true);
        course.setItemLabelGenerator(Course::getName);
        course.addValueChangeListener(e->setSectionItems());

        section.setLabel("Section");
        section.setRequiredIndicatorVisible(true);
        section.setItemLabelGenerator(Section::getName);
        section.addValueChangeListener(e->updateDownloadExcelResource());
/*
        semester.setLabel("Semester");
        semester.setRequiredIndicatorVisible(true);
        semester.setDataProvider(new CrudServiceDataProvider<Semester, Void>(this.semesterService));
        semester.setItemLabelGenerator(Semester::getName);
        semester.addValueChangeListener(e->updateDownloadExcelResource());
*/
        formLayout.add(course, section/*, semester*/);
        return formLayout;
    }

    private void updateDownloadExcelResource() {
        if( section.getValue()!=null /*&& semester.getValue()!=null*/ ) {
            StreamResource resource = new StreamResource(getExcelFileName(),
                    () -> getDownloadExcelInputStream());
            resource.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resource.setCacheTime(1); // disable caching
            generateExcelAnchor.setHref(resource);
            generateExcelAnchor.setEnabled(true);
            importStudentGrades.setEnabled(true);

        } else {
            generateExcelAnchor.setEnabled(false);
            importStudentGrades.setEnabled(false);
        }
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");

        generateExcelAnchor.add(generateExcelButton);
        generateExcelAnchor.getElement().setAttribute("download", true);
        generateExcelAnchor.setEnabled(false);
        buttonLayout.add(generateExcelAnchor);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload importUpload = new Upload(buffer);
        importUpload.setUploadButton(importStudentGrades);
        importUpload.setDropAllowed(false);
        importUpload.setAcceptedFileTypes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        importUpload.addSucceededListener(event -> {
            Notification.show("File uploaded:"/*+event.getMIMEType()+" "*/+event.getFileName()+" "+buffer.getFileData().getFileName());
            importExcelData(buffer);
        });
        importStudentGrades.setEnabled(false);
        buttonLayout.add(importUpload);
        return buttonLayout;
    }

    /**
     * Called after succesfull login
     * @param id null when an Admin user logged in else Instructor.id of the logged in Instructor
     */
    public void setInstructorId(Integer id) {
        this.instructorId = id;
        setCourseItems(id);
    }

    private void setCourseItems(Integer instructorId) {
        course.setItems(this.sectionService.findByInstructorId(instructorId).stream().map(section-> section.getCourse()));
    }

    private void setSectionItems() {
        section.setItems(this.sectionService.findByInstructorId(instructorId).stream().filter(section->section.getCourse().equals(course.getValue())));
    }

    private String getExcelFileName() {
        String fileName = "";
        try {
            fileName = section.getValue().getCourse().getName() + "_" + section.getValue().getName() /*+ "_" + semester.getValue().getName()*/;
            fileName = URLEncoder.encode(fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  fileName + ".xlsx";
    }

    private InputStream getDownloadExcelInputStream() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet1 = wb.createSheet("import_data");
        Row row = sheet1.createRow(0);
        row.createCell(0).setCellValue("Student ID");
        row.createCell(1).setCellValue("Name");
        row.createCell(2).setCellValue("Quiz Grade");
        row.createCell(3).setCellValue("Mid Grade");
        row.createCell(4).setCellValue("Project Grade");
        row.createCell(5).setCellValue("Home work");
        row.createCell(6).setCellValue("Total");

        // generate test data
        /*
        Random rnd = new Random();
        for(int i=1; i<1000; i++) {
            Row r = sheet1.createRow(i);
            r.createCell(0).setCellValue(String.valueOf(i));
            r.createCell(1).setCellValue("studentName"+i);
            r.createCell(2).setCellValue(rnd.nextInt(25));
            r.createCell(3).setCellValue(rnd.nextInt(25));
            r.createCell(4).setCellValue(rnd.nextInt(25));
            r.createCell(5).setCellValue(rnd.nextInt(25));
            r.createCell(6).setCellFormula("SUM(C"+(i+1)+":F"+(i+1)+")");
        }
         */
        Row r = sheet1.createRow(1);
        r.createCell(6).setCellFormula("SUM(C2:F2)");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            wb.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void importExcelData(MemoryBuffer buffer) {
        try {
            Notification.show("Importing data...");
            Workbook workbook = new XSSFWorkbook(buffer.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            StudentGradeList studentGradeList = new StudentGradeList();
            studentGradeList.setSection(this.section.getValue());
            //studentGradeList.setSemester(this.semester.getValue());
            List<StudentGrade> gradeList = new ArrayList();
            int dataNum = 0;
            int rowIdx = 0;
            for (Row row : sheet) {
                if( rowIdx >  0 ) {
                    StudentGrade studentGrade = new StudentGrade();
                    studentGrade.setStudentGradeList(studentGradeList);
                    int colIdx = 0;
                    for (Cell cell : row) {
                        switch (colIdx) {
                            case 0:
                                try {
                                    studentGrade.setStudentId(""+Double.valueOf(cell.getNumericCellValue()).intValue());
                                } catch(Exception ex) {
                                    try {
                                        studentGrade.setStudentId(cell.getStringCellValue());
                                    } catch (Exception ex2) {
                                        ex2.printStackTrace();
                                    }
                                }
                                break;
                            case 1:
                                studentGrade.setStudentName(cell.getStringCellValue());
                                break;
                            case 2:
                                try {
                                    studentGrade.setQuizGrade(Double.valueOf(cell.getNumericCellValue()).intValue());
                                } catch(Exception ex) {
                                    ex.printStackTrace();
                                    Notification.show("Ivalid quizGrade in row "+rowIdx);
                                    studentGrade.setQuizGrade(0);
                                }
                                break;
                            case 3:
                                try {
                                    studentGrade.setMidGrade(Double.valueOf(cell.getNumericCellValue()).intValue());
                                } catch(Exception ex) {
                                    ex.printStackTrace();
                                    Notification.show("Ivalid midGrade in row "+rowIdx);
                                    studentGrade.setMidGrade(0);
                                }
                                break;
                            case 4:
                                try {
                                    studentGrade.setProjectGrade(Double.valueOf(cell.getNumericCellValue()).intValue());
                                } catch(Exception ex) {
                                    ex.printStackTrace();
                                    Notification.show("Ivalid projectGrade in row "+rowIdx);
                                    studentGrade.setProjectGrade(0);
                                }
                                break;
                            case 5:
                                try {
                                    studentGrade.setHomeWork(Double.valueOf(cell.getNumericCellValue()).intValue());
                                } catch(Exception ex) {
                                    ex.printStackTrace();
                                    Notification.show("Ivalid homeWork in row "+rowIdx);
                                    studentGrade.setHomeWork(0);
                                }
                                break;
                        }
                        colIdx++;
                    }
                    gradeList.add(studentGrade);
                    dataNum++;
                }
                rowIdx++;
            }
            studentGradeList.setGradeList(gradeList);
            studentGradeListService.update(studentGradeList);
            Notification.show("Import finished. Imported #:"+dataNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
