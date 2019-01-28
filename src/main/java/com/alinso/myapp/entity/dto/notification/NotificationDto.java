package com.alinso.myapp.entity.dto.notification;

import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.NotificationType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class NotificationDto {

    private ProfileDto trigger;

    private ProfileDto target;

    private String createdAtString;

    @Enumerated(EnumType.ORDINAL)
    private NotificationType notificationType;

    private Boolean isRead;

    private String message;  //for general type notifications

    public ProfileDto getTrigger() {
        return trigger;
    }

    public void setTrigger(ProfileDto trigger) {
        this.trigger = trigger;
    }

    public ProfileDto getTarget() {
        return target;
    }

    public void setTarget(ProfileDto target) {
        this.target = target;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAtString() {
        return createdAtString;
    }

    public void setCreatedAtString(String createdAtString) {
        this.createdAtString = createdAtString;
    }
}
