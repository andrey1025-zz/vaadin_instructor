package com.petrz.instructors.data.entity;
import javax.persistence.Entity;

import com.petrz.instructors.data.AbstractEntity;


/**
 * Administrator user
 */
@Entity
public class Admin extends AbstractEntity {

    private String name;
    private String email;
    private String pwd; // password
    private Integer customId;

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

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getCustomId() {
        return customId;
    }

    public void setCustomId(Integer customId) {
        this.customId = customId;
    }
}

