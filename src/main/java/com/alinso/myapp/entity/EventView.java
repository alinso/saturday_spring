package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EventView extends BaseEntity {

    @ManyToOne
    private User viewer;

    @ManyToOne
    private Event event;

    public User getViewer() {
        return viewer;
    }

    public void setViewer(User viewer) {
        this.viewer = viewer;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
