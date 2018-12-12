package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Block extends BaseEntity {

    @OneToOne
    @NotNull
    private User blocker;

    @OneToOne
    @NotNull
    private User blocked;

    public User getBlocker() {
        return blocker;
    }

    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }

    public User getBlocked() {
        return blocked;
    }

    public void setBlocked(User blocked) {
        this.blocked = blocked;
    }
}
