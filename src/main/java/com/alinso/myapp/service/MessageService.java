package com.alinso.myapp.service;

import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.message.ConversationDto;
import com.alinso.myapp.entity.dto.message.MessageDto;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MessageRepository;
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
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    BlockService blockService;

    public MessageDto send(MessageDto messageDto) {
        Message message = modelMapper.map(messageDto, Message.class);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader = userService.findEntityById(messageDto.getReader().getId());


        if (blockService.isThereABlock(reader.getId()))
            throw new UserWarningException("Erişim Yok");


        message.setWriter(writer);
        message.setReader(reader);

        messageRepository.save(message);

        userEventService.newMessage(message.getReader());
        messageDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "DD/MM HH:mm"));
        messageDto.setReader(userService.toProfileDto(message.getReader()));
        return messageDto;
    }


    public List<MessageDto> getMessagesForReader(Long readerId) {

        User reader = userService.findEntityById(readerId);
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Message> messages = messageRepository.getByReaderWriter(reader, writer);


        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {

            MessageDto messageDto = new MessageDto();
            messageDto.setMessage(message.getMessage());
            messageDto.setReader(userService.toProfileDto(message.getReader()));
            messageDto.setCreatedAt(DateUtil.dateToString(message.getCreatedAt(), "DD/MM HH:mm"));
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    private Long getOppositeId(Message message, User me) {
        Long oppositeId;
        if (message.getReader().getId() == me.getId()) {
            oppositeId = message.getWriter().getId();
        } else {
            oppositeId = message.getReader().getId();
        }
        return oppositeId;
    }


    public List<ConversationDto> getMyConversations() {
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //read sql dtos from database
        List<Message> latestMessageFromEachConversation = messageRepository.latestMessageFromEachConversation(me);

        //we wont get two way latest message of same conversation
        //so for every conversation we will have OPPOSITEID
        List<Long> oppositeIds = new ArrayList<>();

        List<ConversationDto> myConversationDtos = new ArrayList<>();
        for (Message message : latestMessageFromEachConversation) {


            //define the opposite id for every conversation
            Long oppositeId = getOppositeId(message, me);

            //if opposite id exists, it means that we have added last message of this conversation
            if (!oppositeIds.contains(oppositeId))
                oppositeIds.add(oppositeId);
            else
                continue;

            if (blockService.isThereABlock(oppositeId))
                continue;


            User oppositeUser = userService.findEntityById(oppositeId);

            ConversationDto conversationDto = new ConversationDto();
            conversationDto.setReader(null);
            conversationDto.setWriter(null);
            conversationDto.setLastMessage(message.getMessage());
            conversationDto.setProfileDto(userService.toProfileDto(oppositeUser));

            myConversationDtos.add(conversationDto);

        }

        userEventService.messaesRead();
        return myConversationDtos;
    }


    public void greetingMessageForNewUser(User reader) {

        //this will be changed in future
        ProfileInfoForUpdateDto ali = userService.findByEmail("soyaslanaliinsan@gmail.com");
        /////
        Message message = new Message();
        message.setReader(reader);
        message.setWriter(userService.findEntityById(ali.getId()));
        message.setMessage("Aramıza Hoşgeldin, ilk kullanıcılarımızdan biri olduğun için çok teşekkür ederiz:) ilk 500 kişiden bir olduğun için 1 ay premium üyelik hediye ediyoruz \n" +
                " Activity Friend sayesinde bir şey yapacağın zaman yalnız kalmak istemezsen bunu paylaşabilir ve aktivitende(yemek yemek, dışarı çıkmak, sinemaya gitmek vs...) sana eşlik edecek kişiler bulabilirsin." +
                " Üstelik sen de başkalarının aktivitelerine katılabilir, yeni insanlarla tanışabilirsin." +
                "\n" +
                "\n" +
                " Activiy Friend kadın-erkek sayısı dengeli, tüm kullanıcıların referansla üye olabildiği bir sistemdir. Herhangi biriyle birşey yapmadan önce o kişi " +
                " hakkında yazılanları okuyabilir, katıldığı aktivieleri görebilirsin. Ayrıca kişinin puanı da güvenilirliği hakkında fikir verebilir." +
                "\n Sormak istediğin herhangi birşey olursa buradan yazabilirsin, yardımcı olmaktan mutluluk duyarız." +
                "\n" +
                "İyi eğlenceler, dileriz");
        messageRepository.save(message);
    }
}






























