package com.petrz.instructors.data.entity;

import com.petrz.instructors.data.AbstractEntity;

import javax.persistence.*;

@Entity
@Table(name = "studentgrade")
public class StudentGrade extends AbstractEntity {

    /**
     * List which this studentGrade belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENTGRADELIST_ID")
    private StudentGradeList studentGradeList;
    private String studentId;
    private String studentName;
    private Integer quizGrade;
    private Integer midGrade;
    private Integer projectGrade;
    private Integer homeWork;

    public StudentGradeList getStudentGradeList() {
        return studentGradeList;
    }

    public void setStudentGradeList(StudentGradeList studentGradeList) {
        this.studentGradeList = studentGradeList;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getQuizGrade() {
        return quizGrade;
    }

    public void setQuizGrade(Integer quizGrade) {
        this.quizGrade = quizGrade;
    }

    public Integer getMidGrade() {
        return midGrade;
    }

    public void setMidGrade(Integer midGrade) {
        this.midGrade = midGrade;
    }

    public Integer getProjectGrade() {
        return projectGrade;
    }

    public void setProjectGrade(Integer projectGrade) {
        this.projectGrade = projectGrade;
    }

    public Integer getHomeWork() {
        return homeWork;
    }

    public void setHomeWork(Integer homeWork) {
        this.homeWork = homeWork;
    }
}