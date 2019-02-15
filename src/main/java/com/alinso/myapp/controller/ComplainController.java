package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.complain.ComplainDto;
import com.alinso.myapp.service.ComplainService;
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
@RequestMapping("/complain")
public class ComplainController {

    @Autowired
    ComplainService  complainService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;


    @PostMapping("/create")
    public ResponseEntity<?> save(@RequestBody @Valid ComplainDto complainDto, BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        complainService.create(complainDto);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

}
