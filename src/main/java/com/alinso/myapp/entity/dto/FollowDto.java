package com.alinso.myapp.entity.dto;

import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.FollowStatus;

public class FollowDto {

    private Long id;
    private ProfileDto follower;
    private FollowStatus status;


    public ProfileDto getFollower() {
        return follower;
    }

    public void setFollower(ProfileDto follower) {
        this.follower = follower;
    }

    public FollowStatus getStatus() {
        return status;
    }

    public void setStatus(FollowStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
