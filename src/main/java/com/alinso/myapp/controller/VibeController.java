package com.alinso.myapp.controller;


import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.vibe.VibeDto;
import com.alinso.myapp.entity.enums.VibeType;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.ActivityRequestService;
import com.alinso.myapp.service.VibeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("vibe/")
public class VibeController {


    @Autowired
    VibeService vibeService;

    @Autowired
    ActivityRequestService activityRequestService;


    @Autowired
    UserRepository userRepository;

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody VibeDto vibeDto) {
        vibeService.save(vibeDto);

        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @GetMapping("vibePercent/{userId}")
    public ResponseEntity<?> vibePercent(@PathVariable("userId") Long userId){
        Integer vibe  =vibeService.calculateVibe(userId);
        return new ResponseEntity<Integer>(vibe,HttpStatus.OK);
    }


    @GetMapping("vibePercentOfActivityOwner/{activityId}")
    public ResponseEntity<?> vibePercentOfActivityOwner(@PathVariable("activityId") Long activityId){
        Integer vibe  =vibeService.calculateVibeOfActivityOwner(activityId);
        return new ResponseEntity<Integer>(vibe,HttpStatus.OK);
    }

    @GetMapping("usersICanVibe")
    public ResponseEntity<?> usersICanVibe(){
        List<ProfileDto> profileDtoList  = vibeService.userICanVibe();
        return new ResponseEntity<List<ProfileDto>>(profileDtoList,HttpStatus.OK);
    }


    @GetMapping("vibePercentOfRequestOwner/{requestId}")
    public ResponseEntity<?> vibePercentOfRequestOwner(@PathVariable("requestId") Long requestId){
        Integer vibe  =vibeService.vibePercentOfRequestOwner(requestId);
        return new ResponseEntity<Integer>(vibe,HttpStatus.OK);
    }
    @GetMapping("vibeCountOfUser/{userId}")
    public ResponseEntity<?> vibeCountOfUser(@PathVariable("userId") Long userId){
        Integer vibe  =vibeService.vibeCountOfUser(userId);
        return new ResponseEntity<Integer>(vibe,HttpStatus.OK);
    }


    @GetMapping("myVibeOfThisUser/{userId}")
    public ResponseEntity<?> myVibeOfThisUser(@PathVariable("userId") Long userId){

        VibeType vibeType = vibeService.myVibeOfThisUser(userId);

        return new ResponseEntity<VibeType>(vibeType, HttpStatus.OK);
    }

    @GetMapping("haveTheseUsersEverMeet/{user2Id}")
    public ResponseEntity<?> haveTheseUsersEverMeet(@PathVariable("user2Id") Long user2Id) {

        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean result = activityRequestService.haveTheseUsersMeetAllTimes(user1.getId(), user2Id);

        return new ResponseEntity<Boolean>(result,HttpStatus.OK);
    }

}
