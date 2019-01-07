package com.alinso.myapp.service;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserStatsService {


    private final Integer NEW_MEETING_POINT =1;

    @Autowired
    UserRepository userRepository;

    public void newMeetingCreated(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPoint((user.getPoint()+ NEW_MEETING_POINT));
        user.setMeetingCount((user.getMeetingCount() + 1));
        userRepository.save(user);
    }

    public void meetingDeleted(){
        User user  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPoint((user.getPoint()+ NEW_MEETING_POINT));
        user.setMeetingCount((user.getMeetingCount() - 1));
        userRepository.save(user);
    }


}
