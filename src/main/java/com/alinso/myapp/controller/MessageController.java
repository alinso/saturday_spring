package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.message.ConversationDto;
import com.alinso.myapp.entity.dto.message.MessageDto;
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


    @GetMapping("/conversations")
    public ResponseEntity<?> conversations(){
        List<ConversationDto> conversationDtos = messageService.getMyConversations();

        return new ResponseEntity<>(conversationDtos,HttpStatus.OK);
    }

}
