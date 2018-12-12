package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Message extends BaseEntity {

    @Column
    @NotBlank
    private String message;


    @ManyToOne
    @NotNull
    private Conversation conversationEntity;

    @ManyToOne
    @NotNull
    private User writer;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Conversation getConversationEntity() {
        return conversationEntity;
    }

    public void setConversationEntity(Conversation conversationEntity) {
        this.conversationEntity = conversationEntity;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }
}
