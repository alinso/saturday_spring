package com.alinso.myapp.controller;


import com.alinso.myapp.entity.Application;
import com.alinso.myapp.service.ApplicationService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.ApplicationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("application")
public class ApplicationController {

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    ApplicationValidator applicationValidator;

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody Application application, BindingResult result) {

        applicationValidator.validate(application, result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;


        applicationService.save(application);
        return new ResponseEntity<>("Saved", HttpStatus.OK);
    }

    @GetMapping("/all/{pageNum}")
    public ResponseEntity<?> save(@PathVariable("pageNum") int pageNum) {
        List<Application> applications = applicationService.all(pageNum);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/action/{result}/{id}")
    public ResponseEntity<?> action(@PathVariable("result") String result, @PathVariable("id") Long id) {
        if (result.equals("approve"))
            applicationService.approve(id);
        if (result.equals("decline"))
            applicationService.decline(id);

        return new ResponseEntity<>("saved", HttpStatus.OK);
    }


}








