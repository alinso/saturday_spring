package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.service.DiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("sdklsdf")
@RestController
public class AdminController {

    @Autowired
    DiscoverService discoverService;


    @GetMapping("dashboard")
    public ResponseEntity<?> dashboard() {
        return new ResponseEntity<String>("okk", HttpStatus.OK);
    }


    @PostMapping("createDiscover")
    public ResponseEntity<?> saveEvent(@Valid DiscoverDto discoverDto){
        discoverService.save(discoverDto);
        return new ResponseEntity<>("okk",HttpStatus.OK);
    }

    @PostMapping("updateDiscover")
    public ResponseEntity<?> updateDiscover(@Valid DiscoverDto discoverDto){
        discoverService.update(discoverDto);
        return new ResponseEntity<>("okk",HttpStatus.OK);
    }
}