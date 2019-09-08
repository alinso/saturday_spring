package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.ActivityRequestStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class ActivityRequest extends BaseEntity{

    @ManyToOne
    @NotNull
    private Activity activity;


    @ManyToOne
    @NotNull
    private User applicant;


    @Column
    @Enumerated(EnumType.ORDINAL)
    private ActivityRequestStatus activityRequestStatus;

    @Column
    private Integer result;


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public ActivityRequestStatus getActivityRequestStatus() {
        return activityRequestStatus;
    }

    public void setActivityRequestStatus(ActivityRequestStatus activityRequestStatus) {
        this.activityRequestStatus = activityRequestStatus;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
