package com.alinso.myapp.entity.dto.message;

import com.alinso.myapp.entity.dto.user.ProfileDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class    MessageActivityDto {

    @NotNull
    private long activityId;

    @NotBlank(message = "Mesaj boş olamaz")
    private String message;


    private ProfileDto writer;

    private String createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProfileDto getWriter() {
        return writer;
    }

    public void setWriter(ProfileDto writer) {
        this.writer = writer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }
}
