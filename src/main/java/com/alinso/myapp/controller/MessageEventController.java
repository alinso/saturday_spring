package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.message.MessageEventDto;
import com.alinso.myapp.service.MessageEventService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.MessageEventValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("messageEvent/")
public class MessageEventController {
    @Autowired
    MessageEventService messageEventService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    MessageEventValidator messageEventValidator;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody MessageEventDto messageEventDto, BindingResult result){

        messageEventValidator.validate(messageEventDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        MessageEventDto newMessageDto  = messageEventService.send(messageEventDto);

        return new ResponseEntity<>(newMessageDto, HttpStatus.ACCEPTED);
    }
    @GetMapping("/getMessages/{id}")
    public ResponseEntity<?> getMessages(@PathVariable("id") Long id){

        List<MessageEventDto> messageEventDtos = messageEventService.getMessagesOfEvent(id);

        return new ResponseEntity<>(messageEventDtos, HttpStatus.ACCEPTED);
    }




}
