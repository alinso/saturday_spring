package com.alinso.myapp.controller;


import com.alinso.myapp.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invitation")
public class InvitationController {

    @Autowired
    InvitationService invitationService;

    @GetMapping("invite/{activityId}/{readerId}")
    public ResponseEntity<?> findActivities(@PathVariable("activityId") Long activityId, @PathVariable("readerId") Long readerId){

        invitationService.send(readerId,activityId);

        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }
}
