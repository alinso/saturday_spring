package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.EventApplicantStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class EventApplicant extends BaseEntity{

    @OneToOne
    @NotNull
    private Event event;


    @OneToOne
    @NotNull
    private User applicant;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private EventApplicantStatus eventApplicantStatus;


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

    public EventApplicantStatus getEventApplicantStatus() {
        return eventApplicantStatus;
    }

    public void setEventApplicantStatus(EventApplicantStatus eventApplicantStatus) {
        this.eventApplicantStatus = eventApplicantStatus;
    }
}
