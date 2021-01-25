package com.alinso.myapp.entity;


import com.alinso.myapp.entity.enums.ApplicationStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

//applied to be a user, wanting a reference code
@Entity
public class Application extends BaseEntity{

    @NotBlank(message = "Name can't be empty")
    @Column
    private String name;

    @NotBlank(message = "Surame can't be empty")
    @Column
    private String surname;


    @NotBlank(message = "Phone can't be empty")
    @Column
    private String phone;

    @Column
    private String referenceCode;

    @NotBlank(message = "About can't be empty")
    @Column
    private String about;

    @Column
    private ApplicationStatus applicationStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
