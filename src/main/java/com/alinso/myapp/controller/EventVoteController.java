package com.alinso.myapp.controller;


import com.alinso.myapp.service.EventVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventVote")
public class EventVoteController {

    @Autowired
    EventVoteService eventVoteService;

    @GetMapping("/save/{eventId}/{type}")
    public ResponseEntity<?>  save(@PathVariable("eventId") Long eventId, @PathVariable("type") String type) {
        int totalVote   = eventVoteService.saveVote(eventId, type);
        return new ResponseEntity<>(totalVote,HttpStatus.OK);
    }

    @GetMapping("/eventTotal/{eventId}")
    public ResponseEntity<?> eventTotal(@PathVariable("eventId") Long eventId) {
        int res = eventVoteService.eventTotal(eventId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/myVote/{eventId}")
    public ResponseEntity<?> myVote(@PathVariable("eventId") Long eventId) {
        int res = eventVoteService.myVote(eventId);
        return new ResponseEntity<Integer>(res, HttpStatus.OK);
    }




}
