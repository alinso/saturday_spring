package com.alinso.myapp.controller;

import com.alinso.myapp.entity.enums.EventRequestResult;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.service.EventRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("request")
public class EventRequestController {

    @Autowired
    EventRequestService eventRequestService;

    @GetMapping("sendRequest/{id}")
    public ResponseEntity<?> join(@PathVariable("id") Long id){

        Integer isThisUserJoins = eventRequestService.sendRequest(id);

        return new ResponseEntity<>(isThisUserJoins, HttpStatus.OK);
    }

    @GetMapping("approveRequest/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable("id") Long id){

        EventRequestStatus status = eventRequestService.approveRequest(id);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }


    @GetMapping("requestResult/{requestId}/{result}")
    public ResponseEntity<?> saveResults(@PathVariable("requestId") Long requestId, @PathVariable("result") EventRequestResult result){
        eventRequestService.saveResult(requestId,result);

        return new ResponseEntity<>("SAVED",HttpStatus.OK);
    }


}
