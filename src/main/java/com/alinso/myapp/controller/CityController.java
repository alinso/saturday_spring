package com.alinso.myapp.controller;

import com.alinso.myapp.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("city")
public class CityController {

    @Autowired
    CityService cityService;


    @GetMapping("/all")
    public ResponseEntity<?> all(){
        return new ResponseEntity<>(cityService.findAllOrderByName(), HttpStatus.OK);
    }
}
