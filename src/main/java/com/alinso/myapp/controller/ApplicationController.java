package com.alinso.myapp.controller;


import com.alinso.myapp.entity.Application;
import com.alinso.myapp.service.ApplicationService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("application")
public class ApplicationController {


    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    ApplicationService applicationService;


    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody Application application, BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;
        applicationService.save(application);
        return new ResponseEntity<>("Saved", HttpStatus.OK);
    }


}
