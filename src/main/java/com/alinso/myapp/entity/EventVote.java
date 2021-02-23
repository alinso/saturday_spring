package com.alinso.myapp.entity;

import javax.persistence.*;

@Entity
public class EventVote extends BaseEntity{

    @ManyToOne
    private Event event;

    @Column
    private Integer vote;


    @ManyToOne
    private User voter;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
