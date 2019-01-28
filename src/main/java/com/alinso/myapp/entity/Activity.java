package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
public class Activity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Bu kısmı boş bırakamazsınız")
    private String detail;


    public Activity(@NotBlank(message = "Bu kısmı boş bırakamazsınız") String detail) {
        this.detail = detail;
    }

    public Activity(@NotBlank(message = "Bu kısmı boş bırakamazsınız") String detail, User creator, List<User> attendants, String photoName) {
        this.detail = detail;
        this.creator = creator;
        this.photoName = photoName;
    }

    public Activity(){}

    @Column
    @NotNull
    private Date deadLine;

    @OneToOne
    private User creator;

    @ManyToOne
    private City city;

    @Column
    private String photoName;

    @Column(columnDefinition = "boolean default false")
    private Boolean isCommentNotificationSent;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Boolean getCommentNotificationSent() {
        return isCommentNotificationSent;
    }

    public void setCommentNotificationSent(Boolean commentNotificationSent) {
        isCommentNotificationSent = commentNotificationSent;
    }
}
