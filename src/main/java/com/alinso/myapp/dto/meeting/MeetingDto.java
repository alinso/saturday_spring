package com.alinso.myapp.dto.meeting;

import com.alinso.myapp.dto.user.ProfileDto;

public class MeetingDto {
    private Long id;
    private ProfileDto profileDto;
    private String detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfileDto getProfileDto() {
        return profileDto;
    }

    public void setProfileDto(ProfileDto profileDto) {
        this.profileDto = profileDto;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
