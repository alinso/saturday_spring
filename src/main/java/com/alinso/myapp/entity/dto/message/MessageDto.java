package com.alinso.myapp.entity.dto.message;

import com.alinso.myapp.entity.dto.user.ProfileDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MessageDto {

    @NotBlank(message = "Mesaj boş olamaz")
    private String message;

    @NotNull
    private ProfileDto reader;

    private String createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProfileDto getReader() {
        return reader;
    }

    public void setReader(ProfileDto reader) {
        this.reader = reader;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
