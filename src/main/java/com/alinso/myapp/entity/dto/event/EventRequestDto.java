package com.alinso.myapp.entity.dto.event;

import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.EventRequestResult;
import com.alinso.myapp.entity.enums.EventRequestStatus;

public class EventRequestDto {

    private Long id;
    private EventRequestStatus eventRequestStatus;
    private ProfileDto profileDto;
    private EventRequestResult result;

    public EventRequestStatus getEventRequestStatus() {
        return eventRequestStatus;
    }

    public void setEventRequestStatus(EventRequestStatus eventRequestStatus) {
        this.eventRequestStatus = eventRequestStatus;
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


    public EventRequestResult getResult() {
        return result;
    }

    public void setResult(EventRequestResult result) {
        this.result = result;
    }
}
