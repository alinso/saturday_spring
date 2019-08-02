package com.alinso.myapp.controller;

import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.service.ActivityRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("request")
public class ActivityRequestController {

    @Autowired
    ActivityRequestService activityRequestService;

    @GetMapping("sendRequest/{id}")
    public ResponseEntity<?> join(@PathVariable("id") Long id){

        Boolean isThisUserJoins = activityRequestService.sendRequest(id);

        return new ResponseEntity<>(isThisUserJoins, HttpStatus.OK);
    }

    @GetMapping("approveRequest/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable("id") Long id){

        ActivityRequestStatus status = activityRequestService.approveRequest(id);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }


}
