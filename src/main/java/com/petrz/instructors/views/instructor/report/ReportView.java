package com.petrz.instructors.views.instructor.report;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.config.*;
import com.github.appreciated.apexcharts.config.chart.StackType;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.Zoom;
import com.github.appreciated.apexcharts.config.grid.Row;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.helper.Series;
import com.petrz.instructors.data.entity.*;
import com.petrz.instructors.data.service.InstructorService;
import com.petrz.instructors.data.service.SectionService;
import com.petrz.instructors.data.service.SemesterService;
import com.petrz.instructors.data.service.StudentGradeListService;
import com.petrz.instructors.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

import java.util.Arrays;
import java.util.Stack;

/**
 * Report view
 * - when Admin user logged in also Instructor can be selected,
 * - when Instructor logged in only its sections can be selected
 */
@Route(value = "instructorreport", layout = MainView.class)
@PageTitle("Report")
@CssImport("styles/views/instructor/report/report-view.css")
public class ReportView extends Div {

    private InstructorService instructorService;
    private SectionService sectionService;
    //private SemesterService semesterService;
    private StudentGradeListService studentGradeListService;
    private Select<Instructor> instructor = new Select<>();
    private Select<Course> course = new Select<>();
    private Select<Section> section = new Select<>();
    //private Select<Semester> semester = new Select<>();
    private Button generateReport = new Button("Show report");
    /**
     * Instructor.id if an instructor user logged in , null if an admin user logged in
     */
    private Integer instructorId;

    ApexCharts apexChart = new ApexCharts();
/*
    Series<Integer> seriesQuizGrade = new Series<Integer>();
    Series<Integer> seriesMidGrade = new Series<Integer>();
    Series<Integer> seriesProjectGrade = new Series<Integer>();*/
    Series<Integer> seriesTotalGrade = new Series<Integer>();
    Chart chart = new Chart();
    DataLabels dataLabels = new DataLabels();
    Stroke stroke = new Stroke();
    TitleSubtitle titleSubtilte = new TitleSubtitle();
    Grid grid = new Grid();
    XAxis xaxis = new XAxis();
    YAxis yaxis = new YAxis();
    Tooltip tooltip = new Tooltip();

    public ReportView(@Autowired InstructorService instructorService,
                      @Autowired SectionService sectionService, /*@Autowired SemesterService semesterService,*/
                       @Autowired StudentGradeListService studentGradeListService) {
        setId("report-view");
        this.instructorService = instructorService;
        this.sectionService = sectionService;
        //this.semesterService = semesterService;
        this.studentGradeListService = studentGradeListService;

        //seriesQuizGrade.setData(new Integer[] {10, 41, 35, 51, 49, 62, 69, 91, 148});
        //seriesMidGrade.setData(new Integer[] {2001, 410, 3, 510, 149, 0, 219, 301, 848});
        //seriesProjectGrade.setData(new Integer[] {100, 241, 735, 50, 232, 162, 66, 390, 2148});
        /*
        seriesQuizGrade.setName("quizGrade");
        seriesMidGrade.setName("midGrade");
        seriesProjectGrade.setName("projectGrade");*/
        seriesTotalGrade.setName("count");

        chart.setHeight("350");
        chart.setType(Type.bar);
        Zoom zoom = new Zoom();
        zoom.setEnabled(false);
        chart.setZoom(zoom);
        dataLabels.setEnabled(true);
        stroke.setCurve(Curve.straight);
        titleSubtilte.setText("Student count by grade");
        titleSubtilte.setAlign(Align.left);
        Row row = new Row();
        row.setColors(Arrays.asList(new String[] {"#f3f3f3", "transparent"}));
        row.setOpacity(0.5);
        grid.setRow(row);
        xaxis.setCategories(Arrays.asList(new String[] {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"}));
        yaxis.setFloating(false);
        tooltip.setEnabled(false);

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        //apexChart.setSeries(seriesQuizGrade,seriesMidGrade,seriesProjectGrade);
        apexChart.setSeries(seriesTotalGrade);
        apexChart.setChart(chart);
        apexChart.setDataLabels(dataLabels);
        apexChart.setStroke(stroke);
        apexChart.setTitle(titleSubtilte);
        apexChart.setGrid(grid);
        apexChart.setXaxis(xaxis);
        apexChart.setYaxis(yaxis);
        add(apexChart);
    }

    private Component createTitle() {
        return new H3("Reports ");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();

        instructor.setLabel("Instructor");
        instructor.setRequiredIndicatorVisible(true);
        instructor.setDataProvider(new CrudServiceDataProvider<Instructor, Void>(this.instructorService));
        instructor.setItemLabelGenerator(Instructor::getName);
        instructor.addValueChangeListener(e->setCourseItems(instructor.getValue().getId()));

        course.setLabel("Course");
        course.setRequiredIndicatorVisible(true);
        course.setItemLabelGenerator(Course::getName);
        course.addValueChangeListener(e->setSectionItems());

        section.setLabel("Section");
        section.setRequiredIndicatorVisible(true);
        section.setItemLabelGenerator(Section::getName);
        section.addValueChangeListener(e->updateGenerateButtonEnabledState());
/*
        semester.setLabel("Semester");
        semester.setRequiredIndicatorVisible(true);
        semester.setDataProvider(new CrudServiceDataProvider<Semester, Void>(this.semesterService));
        semester.setItemLabelGenerator(Semester::getName);
        semester.addValueChangeListener(e->updateGenerateButtonEnabledState());
*/
        generateReport.addClickListener(e-> {
            updateData();
        });

        formLayout.add(instructor, course, section/*, semester*/);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        generateReport.setEnabled(false);
        buttonLayout.add(generateReport);
        return buttonLayout;
    }

    public void setInstructorId(Integer id) {
        this.instructorId = id; // null if Admin user logged in, not null if an Instructor user logged in
        instructor.setVisible(id==null); // set instructor selection visible if Admin user logged in
        setCourseItems(id);
    }

    private void setCourseItems(Integer instructorId) {
        course.setItems(this.sectionService.findByInstructorId(instructorId).stream().map(section-> section.getCourse()));
    }

    private void setSectionItems() {
        section.setItems(this.sectionService.findByInstructorId(
                instructor.isVisible() ? instructor.getValue().getId() // admin user logged in, instructor selected by the instructor selectionbox
                        : instructorId /* id of the logged in instructor */ )
                .stream().filter(section->section.getCourse().equals(course.getValue())));
    }

    private void updateGenerateButtonEnabledState() {
        generateReport.setEnabled(course.getValue()!=null && section.getValue()!=null /*&& semester.getValue()!=null*/);
    }

    private void updateData() {

        // "A+" : 100..96, "A" : 95..91, "B+" : 90..86, "B" : 85..81, "C+" : 80..76, "C" : 75..71, "D+" : 70..66, "D" : 65..61, "F" : 60..0
        // idx: A+=0 , A=1 , B+=2 , B=3 , C+=4 , C=5 , D+=6 , D=7 , F=8

        //StudentGradeList gradeList = studentGradeListService.findBySectionIdAndSemesterId(section.getValue().getId(),semester.getValue().getId());
        StudentGradeList gradeList = studentGradeListService.findBySectionId(section.getValue().getId());

        /*Integer[] quizGradeCounts = new Integer[] {0,0,0,0,0,0,0,0,0};
        Integer[] midGradeCounts = new Integer[] {0,0,0,0,0,0,0,0,0};
        Integer[] projectGradeCounts = new Integer[] {0,0,0,0,0,0,0,0,0};*/
        Integer[] totalGradeCounts = new Integer[] {0,0,0,0,0,0,0,0,0};

        if( gradeList==null || gradeList.getGradeList()==null ) {
            yaxis.setMax(10);
            Notification.show("No data");

        } else {

            yaxis.setMax(gradeList.getGradeList().size());

            /*
            gradeList.getGradeList().forEach(studentGrade -> {
                int grade = studentGrade.getQuizGrade().intValue();
                int idx = grade <= 60 ? 8 : (100 - grade) / 5;
                quizGradeCounts[idx]++;
            });

            gradeList.getGradeList().forEach(studentGrade -> {
                int grade = studentGrade.getMidGrade().intValue();
                int idx = grade <= 60 ? 8 : (100 - grade) / 5;
                midGradeCounts[idx]++;
            });

            gradeList.getGradeList().forEach(studentGrade -> {
                int grade = studentGrade.getProjectGrade().intValue();
                int idx = grade <= 60 ? 8 : (100 - grade) / 5;
                projectGradeCounts[idx]++;
            });*/

            gradeList.getGradeList().forEach(studentGrade -> {
                int totalGrade = studentGrade.getQuizGrade().intValue()
                        +studentGrade.getMidGrade().intValue()
                        +studentGrade.getProjectGrade().intValue()
                        +studentGrade.getHomeWork().intValue();
                int idx = totalGrade <= 60 ? 8 : (100 - totalGrade) / 5;
                totalGradeCounts[idx]++;
            });


        }

        /* test only
        seriesQuizGrade.setData(new Integer[] {20, 21, 70, 26, 99, 31, 138, 46, 298});
        seriesMidGrade.setData(new Integer[] {1001, 810, 2, 1020, 70, 0, 419, 151, 1648});
        seriesProjectGrade.setData(new Integer[] {50, 441, 335, 150, 132, 262, 36, 690, 1048});
         */

        /*
        seriesQuizGrade.setData(quizGradeCounts);
        seriesMidGrade.setData(midGradeCounts);
        seriesProjectGrade.setData(projectGradeCounts);*/
        seriesTotalGrade.setData(totalGradeCounts);

        //apexChart.updateSeries(seriesQuizGrade,seriesMidGrade,seriesProjectGrade);
        apexChart.updateSeries(seriesTotalGrade);
    }

}

