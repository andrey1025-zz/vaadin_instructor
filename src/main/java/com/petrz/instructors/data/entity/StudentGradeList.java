package com.petrz.instructors.data.entity;

import com.petrz.instructors.data.AbstractEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "studentgradelist")
public class StudentGradeList extends AbstractEntity {


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SEMESTER_ID")
    private Semester semester;

    /**
     * section which this list belongs to
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SECTION_ID")
    private Section section;
    /**
     * grade values
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "studentGradeList", cascade = CascadeType.ALL)
    private List<StudentGrade> gradeList;

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<StudentGrade> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<StudentGrade> gradeList) {
        this.gradeList = gradeList;
    }
}