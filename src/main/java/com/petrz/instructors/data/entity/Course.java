package com.petrz.instructors.data.entity;

import com.petrz.instructors.data.AbstractEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "course")
public class Course extends AbstractEntity {

    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "course", cascade = CascadeType.REFRESH)
    private List<Section> sections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}