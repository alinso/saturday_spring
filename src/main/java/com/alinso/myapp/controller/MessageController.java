package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.message.ConversationDto;
import com.alinso.myapp.entity.dto.message.MessageDto;
import com.alinso.myapp.service.ActivityRequestService;
import com.alinso.myapp.service.MessageService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("message/")
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;


    @Autowired
    ActivityRequestService activityRequestService;


    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody  MessageDto messageDto, BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;
         MessageDto  newMessageDto  =messageService.send(messageDto);

        return new ResponseEntity<>(newMessageDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("getMessagesForReader/{id}")
    public ResponseEntity<?> getMessagesForReader(@PathVariable("id") Long readerId){
        List<MessageDto> messageDtos = messageService.getMessagesForReader(readerId);


        return new ResponseEntity<>(messageDtos,HttpStatus.ACCEPTED);
    }


    @GetMapping("/conversations/{pageNum}")
    public ResponseEntity<?> conversations(@PathVariable("pageNum") Integer pageNum){
        List<ConversationDto> conversationDtos = messageService.getMyConversations(pageNum);

        return new ResponseEntity<>(conversationDtos,HttpStatus.OK);
    }


    @GetMapping("deleteConversation/{otherId}")
    public ResponseEntity<?> deleteCoversation(@PathVariable("otherId") Long otherId){
        messageService.deleteConversation(otherId);
        return new ResponseEntity<String>("Ok",HttpStatus.OK);
    }


    @GetMapping("haveTheseUsersMeet/{id1}/{id2}")
    public ResponseEntity<Boolean> haveTheseUsersMeet(@PathVariable("id1") Long id1,@PathVariable("id2") Long id2 ){
        return new ResponseEntity<Boolean>(activityRequestService.haveTheseUsersMeet(id1,id2),HttpStatus.OK);
    }

}
