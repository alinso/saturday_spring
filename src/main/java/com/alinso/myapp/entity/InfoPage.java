package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class InfoPage extends BaseEntity {

    @Column
    private String title;

    @Column(columnDefinition="TEXT")
    private String detail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
