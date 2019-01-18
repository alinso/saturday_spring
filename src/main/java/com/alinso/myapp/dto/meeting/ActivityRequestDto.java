package com.alinso.myapp.dto.meeting;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;

public class ActivityRequestDto {

    private Long id;
    private ActivityRequestStatus activityRequestStatus;
    private ProfileDto profileDto;

    public ActivityRequestStatus getActivityRequestStatus() {
        return activityRequestStatus;
    }

    public void setActivityRequestStatus(ActivityRequestStatus activityRequestStatus) {
        this.activityRequestStatus = activityRequestStatus;
    }

    public ProfileDto getProfileDto() {
        return profileDto;
    }

    public void setProfileDto(ProfileDto profileDto) {
        this.profileDto = profileDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
