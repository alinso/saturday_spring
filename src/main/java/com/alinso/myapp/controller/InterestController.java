package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Interest;
import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("interest")
public class InterestController {

    @Autowired
    InterestService interestService;


    @PostMapping("/saveUserInterests")
    public ResponseEntity<?> saveUserInterests(@Valid @RequestBody List<Long> selectedInterestIds) {

        interestService.setUserInterests(selectedInterestIds);
        return new ResponseEntity<>("ok", HttpStatus.ACCEPTED);
    }

    @GetMapping("/allInterests")
    public ResponseEntity<?> allInterests() {

        List<Interest> interests = interestService.allInterests();
        return new ResponseEntity<>(interests, HttpStatus.ACCEPTED);
    }

    @GetMapping("/myInterests")
    public ResponseEntity<?> myInterests() {
        Set<Interest> interests = interestService.myInterests();
        return new ResponseEntity<>(interests, HttpStatus.ACCEPTED);
    }

    @GetMapping("/events/{id}/{pageNum}")
    public ResponseEntity<?> events(@PathVariable("id") Long id, @PathVariable("pageNum") Integer pageNum) {
        List<EventDto> eventDtoList = interestService.eventsByInterestId(id,pageNum);
        return new ResponseEntity<>(eventDtoList, HttpStatus.ACCEPTED);
    }



}
