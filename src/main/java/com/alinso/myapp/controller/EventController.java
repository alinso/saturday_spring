package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("event")
public class EventController {

    @Autowired
    EventService eventService;


    @GetMapping("findNonExpiredEvents")
    public ResponseEntity<?> findNonExpiredEvents(){
        List<EventDto> eventDtoList  = eventService.findNonExpiredEvents();
        return new ResponseEntity<List<EventDto>>(eventDtoList, HttpStatus.OK);
    }

}
