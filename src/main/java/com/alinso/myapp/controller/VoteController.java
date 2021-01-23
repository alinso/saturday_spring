package com.alinso.myapp.controller;


import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.vote.VoteDto;
import com.alinso.myapp.entity.enums.VoteType;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.EventRequestService;
import com.alinso.myapp.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("vote/")
public class VoteController {


    @Autowired
    VoteService voteService;

    @Autowired
    EventRequestService eventRequestService;


    @Autowired
    UserRepository userRepository;

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody VoteDto voteDto) {
        voteService.save(voteDto);

        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @GetMapping("votePercent/{userId}")
    public ResponseEntity<?> votePercent(@PathVariable("userId") Long userId){
        Integer vote  = voteService.calculateVote(userId);
        return new ResponseEntity<Integer>(vote,HttpStatus.OK);
    }


    @GetMapping("votePercentOfEventOwner/{eventId}")
    public ResponseEntity<?> votePercentOfEventOwner(@PathVariable("eventId") Long eventId){
        Integer vote  = voteService.calculateVoteOfOrganiser(eventId);
        return new ResponseEntity<Integer>(vote,HttpStatus.OK);
    }

    @GetMapping("usersICanVote")
    public ResponseEntity<?> usersICanVote(){
        List<ProfileDto> profileDtoList  = voteService.userICanVote();
        return new ResponseEntity<List<ProfileDto>>(profileDtoList,HttpStatus.OK);
    }


    @GetMapping("votePercentOfRequestOwner/{requestId}")
    public ResponseEntity<?> votePercentOfRequestOwner(@PathVariable("requestId") Long requestId){
        Integer vote  = voteService.votePercentOfRequester(requestId);
        return new ResponseEntity<Integer>(vote,HttpStatus.OK);
    }
    @GetMapping("voteCountOfUser/{userId}")
    public ResponseEntity<?> voteCountOfUser(@PathVariable("userId") Long userId){
        Integer vote  = voteService.voteCountOfUser(userId);
        return new ResponseEntity<Integer>(vote,HttpStatus.OK);
    }


    @GetMapping("myVoteOfThisUser/{userId}")
    public ResponseEntity<?> myVoteOfThisUser(@PathVariable("userId") Long userId){

        VoteType voteType = voteService.myVoteOfThisUser(userId);

        return new ResponseEntity<VoteType>(voteType, HttpStatus.OK);
    }

    @GetMapping("haveTheseUsersEverMeet/{user2Id}")
    public ResponseEntity<?> haveTheseUsersEverMeet(@PathVariable("user2Id") Long user2Id) {

        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean result = eventRequestService.haveTheseUsersMeetAllTimes(user1.getId(), user2Id);

        return new ResponseEntity<Boolean>(result,HttpStatus.OK);
    }

}
