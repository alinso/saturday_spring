package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.entity.dto.message.MessageActivityDto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MessageActivityRepository;
import com.alinso.myapp.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageActivityService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    ActivityService activityService;

    @Autowired
    MessageActivityRepository messageActivityRepository;

    @Autowired
    ActivityRequestService activityRequestService;

    @Autowired
    UserEventService userEventService;

    public MessageActivityDto send(MessageActivityDto messageActivityDto) {
        MessageActivity message = new MessageActivity();
        message.setMessage(messageActivityDto.getMessage());
        Activity activity= activityService.findEntityById(messageActivityDto.getActivityId());


        if(activity.getCreator().getId()==3212 && activity.getId()!=4582 && activity.getId()!=4756)
            throw new  UserWarningException("Bu aktivite mesaj gönderimlerine kapalı. Sorularını özelden sorabilirsin");


        //modelMapper.map(messageActivityDto, MessageActivity.class);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!activityRequestService.isThisUserApprovedTwoDaysLimit(activity))
            throw new UserWarningException("Erişim Yok");

        message.setWriter(writer);
        message.setActivity(activity);

        messageActivityRepository.save(message);

        List<User> approvedUsers  =activityRequestService.findAttendantEntities(activity);
        approvedUsers.add(activity.getCreator());
        for(User approvedUser: approvedUsers) {
            if(approvedUser.getId()!=writer.getId())
            userEventService.newMessageActivity(approvedUser,activity);
        }

        messageActivityDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "dd/MM/YYYY HH:mm"));
        messageActivityDto.setWriter(userService.toProfileDto(writer));
        return messageActivityDto;
    }



    public List<MessageActivityDto> getMessagesOfActivity(Long activityId){

        Activity a  =activityService.findEntityById(activityId);

        if(!activityRequestService.isThisUserApprovedTwoDaysLimit(a))
            throw new UserWarningException("Erişim Yok");


        List<MessageActivity> messageActivityList  = messageActivityRepository.findByActivity(a);
        List<MessageActivityDto> messageActivityDtos  = new ArrayList<>();


        for(MessageActivity messageActivity : messageActivityList ) {
            MessageActivityDto messageActivityDto  =new MessageActivityDto();

            messageActivityDto.setWriter(userService.toProfileDto(messageActivity.getWriter()));
            messageActivityDto.setCreatedAt(DateUtil.dateToString(messageActivity.getCreatedAt(), "dd/MM/YYYY HH:mm"));
            messageActivityDto.setActivityId(activityId);
            messageActivityDto.setMessage(messageActivity.getMessage());
            messageActivityDtos.add(messageActivityDto);
        }

        return messageActivityDtos;

    }
}
































