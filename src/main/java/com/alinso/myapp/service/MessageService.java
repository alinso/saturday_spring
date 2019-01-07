package com.alinso.myapp.service;

import com.alinso.myapp.dto.message.ConversationDto;
import com.alinso.myapp.dto.message.MessageDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.MessageRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;


    public MessageDto send(MessageDto messageDto) {
        Message message = modelMapper.map(messageDto, Message.class);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader = userRepository.findById(messageDto.getReader().getId()).get();

        message.setWriter(writer);
        message.setReader(reader);

       messageRepository.save(message);

        MessageDto newMessageDto =new MessageDto();
        newMessageDto.setMessage(message.getMessage());
        newMessageDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(),"DD/MM HH:mm"));
        newMessageDto.setReader(modelMapper.map(message.getReader(),ProfileDto.class));
            return newMessageDto;
    }


    public List<MessageDto> getMessagesForReader(Long readerId) {

        User reader = userRepository.findById(readerId).get();
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Message> messages = messageRepository.getByReaderWriter(reader, writer);

        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {

            ProfileDto readerDto = modelMapper.map(message.getReader(), ProfileDto.class);

            MessageDto messageDto = new MessageDto();
            messageDto.setMessage(message.getMessage());
            messageDto.setReader(readerDto);
            messageDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "DD/MM HH:mm"));
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    private Long getOppositeId(Message  message,User me){
        Long oppositeId;
        if(message.getReader().getId()==me.getId()){
            oppositeId  = message.getWriter().getId();
        }else{
            oppositeId  = message.getReader().getId();
        }
        return oppositeId;
    }


    public List<ConversationDto> getMyConversations() {
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //read sql dtos from database
        List<Message> latestMessageFromEachConversation = messageRepository.latestMessageFromEachConversation(me);

        //we wont get two way latest message of same conversation
        //so for every conversation we will have OPPOSITEID
        List<Long> oppositeIds  =new ArrayList<>();

        List<ConversationDto> myConversationDtos = new ArrayList<>();
        for (Message message : latestMessageFromEachConversation) {

            //define the opposite id for every conversation
            Long oppositeId = getOppositeId(message,me);

            //if opposite id exists, it means that we have added last message of this conversation
            if(!oppositeIds.contains(oppositeId))
                oppositeIds.add(oppositeId);
            else
                continue;

            User oppositeUser  =userRepository.findById(oppositeId).get();
            ProfileDto oppositeDto = modelMapper.map(oppositeUser,ProfileDto.class);

            ConversationDto conversationDto = new ConversationDto();
            conversationDto.setReader(null);
            conversationDto.setWriter(null);
            conversationDto.setLastMessage(message.getMessage());
            conversationDto.setProfileDto(oppositeDto);

            myConversationDtos.add(conversationDto);

        }
        return myConversationDtos;
    }
}






























