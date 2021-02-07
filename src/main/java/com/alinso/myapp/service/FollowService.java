package com.alinso.myapp.service;

import com.alinso.myapp.entity.Follow;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.FollowDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.FollowStatus;
import com.alinso.myapp.repository.FollowRepository;
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

    public List<FollowDto> findMyFollowings() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Follow> followingUsers = followRepository.findFollowingsOfTheUser(loggedUser);

        List<FollowDto> followDtos = new ArrayList<>();
        for (Follow f  : followingUsers) {
            FollowDto followDto= new FollowDto();
            followDto.setProfileDto(userService.toProfileDto(f.getLeader()));
            followDto.setStatus(f.getStatus());
            followDto.setId(f.getId());

            followDtos.add(followDto);
        }
        return followDtos;
    }

    public List<User> findFollowingsOfUser(User user) {
        List<Follow> followings = followRepository.findFollowingsOfTheUser(user);

        List<User> users= new ArrayList<>();
        for(Follow f:followings){
            User u= f.getLeader();
            users.add(u);
        }
        return users;
    }

    public List<Follow> findFollowersByUser(User user){
        List<Follow> followers = followRepository.findFollowersOfUser(user);
        return followers;
    }

    public List<FollowDto> findMyFollowers(Integer pageNum) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNum, 20);

        List<Follow> followers = followRepository.findFollowersOfUserPaged(loggedUser, pageable);
        List<FollowDto> followDtos = new ArrayList<>();
        for (Follow f : followers) {
            FollowDto followDto = new FollowDto();
            followDto.setProfileDto(userService.toProfileDto(f.getFollower()));
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

    public void remove(Long followId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Follow follow = followRepository.findById(followId).get();

        if (loggedUser.getId() == follow.getLeader().getId()) {
            followRepository.delete(follow);
        }
    }
}




















