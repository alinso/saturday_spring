package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Invitation extends BaseEntity {


    @ManyToOne
    @NotNull
    private Event event;


    @ManyToOne
    @NotNull
    private  User reader;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }
}
