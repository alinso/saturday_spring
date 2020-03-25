package com.alinso.myapp.entity.dto;

import com.alinso.myapp.entity.dto.user.ProfileDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class GhostMessageDto {

    @NotBlank(message = "Mesaj boş olamaz")
    private String message;

    @NotNull
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
}