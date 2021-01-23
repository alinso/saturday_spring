package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.message.MessageEventDto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MessageEventRepository;
import com.alinso.myapp.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageEventService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    EventService eventService;

    @Autowired
    MessageEventRepository messageEventRepository;

    @Autowired
    EventRequestService eventRequestService;

    @Autowired
    UserEventService userEventService;

    public MessageEventDto send(MessageEventDto messageEventDto) {
        MessageEvent message = new MessageEvent();
        message.setMessage(messageEventDto.getMessage());
        Event event = eventService.findEntityById(messageEventDto.getEventId());


        if(event.getCreator().getId()==3212 && event.getId()!=4582 && event.getId()!=4756)
            throw new  UserWarningException("Bu aktivite mesaj gönderimlerine kapalı. Sorularını özelden sorabilirsin");


        //modelMapper.map(messageActivityDto, MessageActivity.class);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!eventRequestService.isThisUserApprovedTwoDaysLimit(event))
            throw new UserWarningException("Erişim Yok");

        message.setWriter(writer);
        message.setEvent(event);

        messageEventRepository.save(message);

        List<User> approvedUsers  = eventRequestService.findAttendantEntities(event);
        approvedUsers.add(event.getCreator());
        for(User approvedUser: approvedUsers) {
            if(approvedUser.getId()!=writer.getId())
            userEventService.newMessageEvent(approvedUser, event);
        }

        messageEventDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "dd/MM/YYYY HH:mm"));
        messageEventDto.setWriter(userService.toProfileDto(writer));
        return messageEventDto;
    }



    public List<MessageEventDto> getMessagesOfEvent(Long eventId){

        Event a  = eventService.findEntityById(eventId);

        if(!eventRequestService.isThisUserApprovedTwoDaysLimit(a))
            throw new UserWarningException("Erişim Yok");


        List<MessageEvent> messageEventList = messageEventRepository.findByEvent(a);
        List<MessageEventDto> messageEventDtos = new ArrayList<>();


        for(MessageEvent messageEvent : messageEventList) {
            MessageEventDto messageEventDto =new MessageEventDto();

            messageEventDto.setWriter(userService.toProfileDto(messageEvent.getWriter()));
            messageEventDto.setCreatedAt(DateUtil.dateToString(messageEvent.getCreatedAt(), "dd/MM/YYYY HH:mm"));
            messageEventDto.setEventId(eventId);
            messageEventDto.setMessage(messageEvent.getMessage());
            messageEventDtos.add(messageEventDto);
        }

        return messageEventDtos;

    }
}
































