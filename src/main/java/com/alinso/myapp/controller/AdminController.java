package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("sdklsdf")
@RestController
public class AdminController {

    @Autowired
    EventService eventService;


    @GetMapping("dashboard")
    public ResponseEntity<?> dashboard() {
        return new ResponseEntity<String>("okk", HttpStatus.OK);
    }


    @PostMapping("saveEvent")
    public ResponseEntity<?> saveEvent(@Valid EventDto eventDto){
        eventService.save(eventDto);
        return new ResponseEntity<>("okk",HttpStatus.OK);
    }
}