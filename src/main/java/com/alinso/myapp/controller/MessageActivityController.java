package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.message.MessageActivityDto;
import com.alinso.myapp.service.MessageActivityService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("messageActivity/")
public class MessageActivityController {
    @Autowired
    MessageActivityService messageActivityService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody MessageActivityDto messageActivityDto, BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;
        MessageActivityDto  newMessageDto  =messageActivityService.send(messageActivityDto);

        return new ResponseEntity<>(newMessageDto, HttpStatus.ACCEPTED);
    }
    @GetMapping("/getMessages/{id}")
    public ResponseEntity<?> getMessages(@PathVariable("id") Long id){

        List<MessageActivityDto> messageActivityDtos = messageActivityService.getMessagesOfActivity(id);

        return new ResponseEntity<>(messageActivityDtos, HttpStatus.ACCEPTED);
    }




}
