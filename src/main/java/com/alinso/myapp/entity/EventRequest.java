package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.EventRequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class EventRequest extends BaseEntity{

    @ManyToOne
    @NotNull
    private Event event;


    @ManyToOne
    @NotNull
    private User applicant;


    @Column
    @Enumerated(EnumType.ORDINAL)
    private EventRequestStatus eventRequestStatus;

    @Column
    private Integer result;


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public EventRequestStatus getEventRequestStatus() {
        return eventRequestStatus;
    }

    public void setEventRequestStatus(EventRequestStatus eventRequestStatus) {
        this.eventRequestStatus = eventRequestStatus;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
