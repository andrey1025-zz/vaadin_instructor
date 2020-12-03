package com.petrz.instructors.data.entity;

import com.petrz.instructors.data.AbstractEntity;

import javax.persistence.*;

import javax.persistence.Entity;

@Entity
@Table(name = "section", indexes = {
        @Index(columnList = "COURSE_ID") ,
        @Index(columnList = "INSTRUCTOR_ID")
})
public class Section extends AbstractEntity {

    private String name;

    /**
     * Course which this section belongs to
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "COURSE_ID")
    private Course course;

    /**
     * Instructor who owns this section
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "INSTRUCTOR_ID")
    private Instructor instructor;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
}