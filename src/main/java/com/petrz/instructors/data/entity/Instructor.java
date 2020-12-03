package com.petrz.instructors.data.entity;
import javax.persistence.*;

import com.petrz.instructors.data.AbstractEntity;

import java.util.List;

/**
 * Instructor user
 */
@Entity
@Table(name = "instructor")
public class Instructor extends AbstractEntity {

    private String name;
    private String email;
    private String customId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "instructor", cascade = CascadeType.REFRESH)
    private List<Section> sections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }


}

