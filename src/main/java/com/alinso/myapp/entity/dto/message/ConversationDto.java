package com.alinso.myapp.entity.dto.message;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;

public class ConversationDto {

    private ProfileDto profileDto;

    private User reader;

    private User writer;

    private String lastMessage;


    public ProfileDto getProfileDto() {
        return profileDto;
    }

    public void setProfileDto(ProfileDto profileDto) {
        this.profileDto = profileDto;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }
}
