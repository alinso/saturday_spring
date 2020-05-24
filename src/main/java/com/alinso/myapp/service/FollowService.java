package com.alinso.myapp.service;

import com.alinso.myapp.entity.Follow;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
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

    public Boolean follow(Long leaderId){

        User follower  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader =userService.findEntityById(leaderId);
        Follow follow = followRepository.findFollowingByLeaderAndFollower(leader,follower);

        Boolean isFollowing;
        if(follow==null){
            Follow newFollow = new Follow();
            newFollow.setFollower(follower);
            newFollow.setLeader(leader);
            followRepository.save(newFollow);
            notificationService.newFollow(leader,follower);
            isFollowing=true;
        }else{
            followRepository.delete(follow);
            isFollowing=false;
        }
        return isFollowing;
    }

    public Boolean isFollowing(Long leaderId) {

        User follower  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader =userService.findEntityById(leaderId);

        Follow follow = followRepository.findFollowingByLeaderAndFollower(leader,follower);
        if(follow==null)
            return false;
        else
            return true;
    }

    public List<ProfileDto> findMyFollowings() {
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> followingUsers = followRepository.findUsersFollowedByTheUser(loggedUser);

        List<ProfileDto> profileDtos  = new ArrayList<>();
        for(User user: followingUsers){
            profileDtos.add(userService.toProfileDto(user));
        }

        return profileDtos;
    }

    public List<User> findFollowingsOfUser(User user) {
        List<User> followingUsers = followRepository.findUsersFollowedByTheUser(user);

        return followingUsers;
    }

    public List<ProfileDto> findMyFollowers(Integer pageNum) {
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNum,20);

        List<User> userList  = followRepository.findFollowersOfUserPaged(loggedUser,pageable);
        return  userService.toProfileDtoList(userList);
    }
}




















