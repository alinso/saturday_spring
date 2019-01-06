package com.alinso.myapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
public class Meeting extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Bu kısmı boş bırakamazsınız")
    private String detail;


    public Meeting(@NotBlank(message = "Bu kısmı boş bırakamazsınız") String detail) {
        this.detail = detail;
    }

    public Meeting(@NotBlank(message = "Bu kısmı boş bırakamazsınız") String detail, User creator, List<User> attendants, String photoName) {
        this.detail = detail;
        this.creator = creator;
        this.photoName = photoName;
    }

    public Meeting(){}

    @Column
    @NotNull
    private Date deadLine;

    @OneToOne
    private User creator;

    @Column
    private String photoName;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }
}
