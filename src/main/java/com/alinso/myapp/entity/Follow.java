package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.FollowStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Follow extends BaseEntity {

    @ManyToOne
    private User leader;

    @ManyToOne
    private User follower;

    @Column
    private FollowStatus status;

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

    public FollowStatus getStatus() {
        return status;
    }

    public void setStatus(FollowStatus status) {
        this.status = status;
    }
}
