package com.alinso.myapp.dto.hashtag;

import com.alinso.myapp.dto.activity.ActivityDto;
import com.alinso.myapp.dto.user.ProfileDto;

public class HashtagDto {
    private String name;
    private ProfileDto profileDto;
    private ActivityDto activityDto;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProfileDto getProfileDto() {
        return profileDto;
    }

    public void setProfileDto(ProfileDto profileDto) {
        this.profileDto = profileDto;
    }

    public ActivityDto getActivityDto() {
        return activityDto;
    }

    public void setActivityDto(ActivityDto activityDto) {
        this.activityDto = activityDto;
    }
}
