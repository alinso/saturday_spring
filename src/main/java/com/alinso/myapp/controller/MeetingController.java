package com.alinso.myapp.controller;


import com.alinso.myapp.dto.meeting.MeetingDto;
import com.alinso.myapp.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import com.alinso.myapp.service.MeetingService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.MeetingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meeting")
public class MeetingController {


    @Autowired
    MeetingService meetingService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    MeetingValidator  meetingValidator;


    @PostMapping("/create")
    public ResponseEntity<?> save(@Valid  MeetingDto meetingDto,BindingResult result){

        meetingValidator.validate(meetingDto,result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        meetingService.save(meetingDto);
        return new ResponseEntity<>(meetingDto,HttpStatus.ACCEPTED);
    }



    @GetMapping("findAll")
    public ResponseEntity<?> findAll(){
        List<MeetingDto>  meetings = meetingService.findAll();

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }

    @GetMapping("join/{id}")
    public ResponseEntity<?> join(@PathVariable("id") Long id){

        Boolean isThisUserJoins = meetingService.join(id);

        return new ResponseEntity<>(isThisUserJoins,HttpStatus.OK);
    }

    @GetMapping("approveRequest/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable("id") Long id){

        MeetingRequestStatus status = meetingService.approveRequest(id);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }


    @GetMapping("requests/{id}")
    public ResponseEntity<?> requests(@PathVariable("id") Long id){
        MeetingDto meetingDto  =meetingService.getMeetingWithRequests(id);
        return new ResponseEntity<>(meetingDto,HttpStatus.OK);
    }


    @GetMapping("findByUserId/{id}")
    public ResponseEntity<?> findByUserId(@PathVariable("id") Long id){
        List<MeetingDto>  meetings = meetingService.findByUserId(id);

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }


    @GetMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        meetingService.deleteById(id);

        return new ResponseEntity<>("deleted",HttpStatus.OK);
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@Valid  MeetingDto meetingDto,BindingResult result){

        meetingValidator.validate(meetingDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;


        meetingService.update(meetingDto);

        return new ResponseEntity<>(meetingDto,HttpStatus.ACCEPTED);

    }

    @GetMapping("findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){

        MeetingDto meetingDto = meetingService.findById(id);

        return new ResponseEntity<>(meetingDto,HttpStatus.OK);
    }




}
