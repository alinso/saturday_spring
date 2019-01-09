package com.alinso.myapp.controller;

import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import com.alinso.myapp.service.MeetingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("request")
public class MeetingRequestController {

    @Autowired
    MeetingRequestService meetingRequestService;

    @GetMapping("sendRequest/{id}")
    public ResponseEntity<?> join(@PathVariable("id") Long id){

        Boolean isThisUserJoins = meetingRequestService.sendRequest(id);

        return new ResponseEntity<>(isThisUserJoins, HttpStatus.OK);
    }

    @GetMapping("approveRequest/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable("id") Long id){

        MeetingRequestStatus status = meetingRequestService.approveRequest(id);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }



}
