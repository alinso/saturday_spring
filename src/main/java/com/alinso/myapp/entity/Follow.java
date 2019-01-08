package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Follow extends BaseEntity {

    @ManyToOne
    private User leader;

    @ManyToOne
    private User follower;

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }
}
