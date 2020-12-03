package com.petrz.instructors.data.entity;

/**
 * logged in user (Administrator or Instructor)
 */
public class UserData {

    private Boolean isAdmin; // true: Admin , false : Instructor
    private String name;
    private String email;
    private String pictureUrl;
    private Integer id; // internal object id : Admin.id or Instructor.id

    public UserData() {
        isAdmin = Boolean.FALSE;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
