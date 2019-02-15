package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Complain extends BaseEntity {

    @Column(columnDefinition="TEXT")
    private String detail;

    @ManyToOne
    private User reporter;

    @ManyToOne
    private User guilty;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getGuilty() {
        return guilty;
    }

    public void setGuilty(User guilty) {
        this.guilty = guilty;
    }
}
