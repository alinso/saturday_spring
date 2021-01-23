package com.alinso.myapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class EventPhoto extends BaseEntity {

    @Column
    @NotNull
    private String fileName;

    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    private Event event;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
