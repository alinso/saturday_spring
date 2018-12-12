package com.alinso.myapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Photo extends BaseEntity{

    @Column
    @NotNull
    private String fileName;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private User user;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
