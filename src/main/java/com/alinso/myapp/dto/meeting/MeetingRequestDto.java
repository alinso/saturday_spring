package com.alinso.myapp.dto.meeting;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;

public class MeetingRequestDto {

    private Long id;
    private MeetingRequestStatus meetingRequestStatus;
    private ProfileDto profileDto;

    public MeetingRequestStatus getMeetingRequestStatus() {
        return meetingRequestStatus;
    }

    public void setMeetingRequestStatus(MeetingRequestStatus meetingRequestStatus) {
        this.meetingRequestStatus = meetingRequestStatus;
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
