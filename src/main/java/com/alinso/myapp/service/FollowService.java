package com.alinso.myapp.service;

import com.alinso.myapp.entity.Follow;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.FollowDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.FollowStatus;
import com.alinso.myapp.repository.FollowRepository;
import com.alinso.myapp.repository.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowService {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    public FollowStatus sendFollowRequest(Long leaderId) {

        User follower = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader = userService.findEntityById(leaderId);
        Follow follow = followRepository.findFollowingByLeaderAndFollower(leader, follower);

        FollowStatus status;
        if (follow == null) {
            Follow newFollow = new Follow();
            newFollow.setFollower(follower);
            newFollow.setLeader(leader);
            newFollow.setStatus(FollowStatus.WAITING);
            followRepository.save(newFollow);
            notificationService.newFollow(leader, follower);
            status = FollowStatus.WAITING;
        } else {
            followRepository.delete(follow);
            status = FollowStatus.NOT_FOLLOWING;
        }
        return status;
    }

    public FollowStatus isFollowing(Long leaderId) {

        User follower = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader = userService.findEntityById(leaderId);

        Follow follow = followRepository.findFollowingByLeaderAndFollower(leader, follower);
        if (follow == null)
            return FollowStatus.NOT_FOLLOWING;
        else
            return follow.getStatus();
    }

    public List<ProfileDto> findMyFollowings() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> followingUsers = followRepository.findUsersFollowedByTheUser(loggedUser);

        List<ProfileDto> profileDtos = new ArrayList<>();
        for (User user : followingUsers) {
            profileDtos.add(userService.toProfileDto(user));
        }

        return profileDtos;
    }

    public List<User> findFollowingsOfUser(User user) {
        List<User> followingUsers = followRepository.findUsersFollowedByTheUser(user);

        return followingUsers;
    }

    public List<FollowDto> findMyFollowers(Integer pageNum) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNum, 20);

        List<Follow> followers = followRepository.findFollowersOfUserPaged(loggedUser, pageable);
        List<FollowDto> followDtos = new ArrayList<>();
        for (Follow f : followers) {
            FollowDto followDto = new FollowDto();
            followDto.setFollower(userService.toProfileDto(f.getFollower()));
            followDto.setStatus(f.getStatus());
            followDto.setId(f.getId());
            followDtos.add(followDto);
        }
        return followDtos;
    }

    public void approve(Long followId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Follow follow = followRepository.findById(followId).get();

        if (loggedUser.getId() == follow.getLeader().getId()) {
            follow.setStatus(FollowStatus.APPROVED);
            followRepository.save(follow);
        }
    }
}




















