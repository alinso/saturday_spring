package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.NotificationType;

import javax.persistence.*;

@Entity
public class Notification extends BaseEntity {

    @ManyToOne
    private User trigger;

    @ManyToOne
    private User target;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private NotificationType notificationType;

    @Column
    private Boolean isRead;

    @Column
    private String message;  //for general type notifications


    public User getTrigger() {
        return trigger;
    }

    public void setTrigger(User trigger) {
        this.trigger = trigger;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
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
}
