package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.FollowDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.FollowStatus;
import com.alinso.myapp.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    FollowService followService;

    @GetMapping("follow/{leaderId}")
    public ResponseEntity<?> follow(@PathVariable("leaderId") Long leaderId){
        FollowStatus result = followService.sendFollowRequest(leaderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("approve/{followId}")
    public ResponseEntity<?> approve(@PathVariable("followId") Long followId){
         followService.approve(followId);
        return new ResponseEntity<>("approved", HttpStatus.OK);
    }
    @GetMapping("remove/{followId}")
    public ResponseEntity<?> remove(@PathVariable("followId") Long followId){
        followService.remove(followId);
        return new ResponseEntity<>("approved", HttpStatus.OK);
    }

    @GetMapping("followStatus/{leaderId}")
    public ResponseEntity<?> isFollowing(@PathVariable("leaderId") Long leaderId){
        FollowStatus result = followService.isFollowing(leaderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("myFollowings")
    public ResponseEntity<?> myFollowings(){

        List<FollowDto> followDtos =followService.findMyFollowings();
        return new ResponseEntity<>(followDtos,HttpStatus.OK);
    }

    @GetMapping("myFollowers/{pageNum}")
    public ResponseEntity<?> myFollowers(@PathVariable("pageNum") Integer pageNum){

        List<FollowDto> followDtos =followService.findMyFollowers(pageNum);
        return new ResponseEntity<>(followDtos,HttpStatus.OK);
    }



}
