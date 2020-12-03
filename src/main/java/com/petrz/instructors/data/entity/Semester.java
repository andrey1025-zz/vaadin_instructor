package com.petrz.instructors.data.entity;

import com.petrz.instructors.data.AbstractEntity;

import javax.persistence.Entity;

@Entity
public class Semester extends AbstractEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}