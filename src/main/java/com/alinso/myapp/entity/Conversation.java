package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Conversation extends BaseEntity {

    @ManyToOne
    @NotNull
    private User initiater;

    @ManyToOne
    @NotNull
    private User replyer;

    public User getInitiater() {

        return initiater;
    }

    public void setInitiater(User initiater) {
        this.initiater = initiater;
    }

    public User getReplyer() {
        return replyer;
    }

    public void setReplyer(User replyer) {
        this.replyer = replyer;
    }
}
