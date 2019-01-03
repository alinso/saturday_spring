package com.alinso.myapp.controller;


import com.alinso.myapp.dto.meeting.MeetingDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.service.MeetingService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {


    @Autowired
    MeetingService meetingService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @PostMapping("/create")
    public ResponseEntity<?> save(@Valid @RequestBody Meeting meeting, BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        meetingService.save(meeting);
        return new ResponseEntity<>(meeting,HttpStatus.ACCEPTED);
    }



    @GetMapping("findAll")
    public ResponseEntity<?> findAll(){
        List<MeetingDto>  meetings = meetingService.findAll();

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }

    @GetMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        meetingService.deleteById(id);

        return new ResponseEntity<>("deleted",HttpStatus.OK);
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@Valid @RequestBody Meeting meeting,BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;


        meetingService.update(meeting);
        MeetingDto meetingDto  =meetingService.findById(meeting.getId());

        return new ResponseEntity<>(meetingDto,HttpStatus.ACCEPTED);

    }

    @GetMapping("findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){

        MeetingDto meetingDto = meetingService.findById(id);

        return new ResponseEntity<>(meetingDto,HttpStatus.OK);
    }




}
