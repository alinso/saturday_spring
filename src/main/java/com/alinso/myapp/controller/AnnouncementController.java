package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.service.DiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("announcement")
public class AnnouncementController {

    @Autowired
    DiscoverService discoverService;


    @GetMapping("findNonExpiredAnnouncements")
    public ResponseEntity<List<DiscoverDto>> findNonExpiredEvents(){
        List<DiscoverDto> discoverDtoList = discoverService.findAll();
        return new ResponseEntity<>(discoverDtoList, HttpStatus.OK);
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<DiscoverDto> findById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(discoverService.findById(id),HttpStatus.OK);
    }

    @GetMapping("findRandom")
    public ResponseEntity<DiscoverDto> findRandom(){
        DiscoverDto discoverDto = discoverService.findRandom();
        return new ResponseEntity<>(discoverDto,HttpStatus.OK);
    }

}
