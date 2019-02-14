package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.event.DiscoverDto;
import com.alinso.myapp.service.DiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("discover")
public class DiscoverController {

    @Autowired
    DiscoverService discoverService;


    @GetMapping("findNonExpiredDiscovers")
    public ResponseEntity findNonExpiredEvents(){
        List<DiscoverDto> discoverDtoList = discoverService.findNonExpiredEvents();
        return new ResponseEntity(discoverDtoList, HttpStatus.OK);
    }

}
