package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.MeetingRequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class MeetingRequest extends BaseEntity{

    @ManyToOne
    @NotNull
    private Meeting meeting;


    @ManyToOne
    @NotNull
    private User applicant;


    @Column
    @Enumerated(EnumType.ORDINAL)
    private MeetingRequestStatus meetingRequestStatus;


    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public MeetingRequestStatus getMeetingRequestStatus() {
        return meetingRequestStatus;
    }

    public void setMeetingRequestStatus(MeetingRequestStatus meetingRequestStatus) {
        this.meetingRequestStatus = meetingRequestStatus;
    }

}
