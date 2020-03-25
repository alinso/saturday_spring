package com.alinso.myapp.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class GhostMessage extends BaseEntity {

    @ManyToOne
    private User writer;


    @Column(columnDefinition = "TEXT")
    private String message;

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
