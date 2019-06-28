package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.service.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("hashtag")
public class HashtagController {

    @Autowired
    HashtagService hashtagService;

    @GetMapping("findActivities/{hashtag}/{pageNum}")
    public ResponseEntity<?> findActivities(@PathVariable("hashtag") String hashtag, @PathVariable("pageNum") Integer pageNum){
        List<ActivityDto> activityDtoList = hashtagService.findActivitiesByHashtag(hashtag,pageNum);
        return new ResponseEntity<List>(activityDtoList, HttpStatus.OK);
    }

    @GetMapping("findUsers/{hashtag}/{pageNum}")
    public ResponseEntity<?> findUsers(@PathVariable("hashtag") String hashtag, @PathVariable("pageNum") Integer pageNum){
        List<ProfileDto> profileDtoList = hashtagService.findUsersByHashtag(hashtag,pageNum);
        return new ResponseEntity<List>(profileDtoList, HttpStatus.OK);
    }

}
