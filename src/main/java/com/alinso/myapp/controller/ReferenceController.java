package com.alinso.myapp.controller;

import com.alinso.myapp.dto.reference.ReferenceDto;
import com.alinso.myapp.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reference")
public class ReferenceController {

    @Autowired
    ReferenceService referenceService;

    @GetMapping("/myReferences")
    public ResponseEntity<?> myReferences(){
        List<ReferenceDto> referenceDtos =  referenceService.getMyReferences();
        return new ResponseEntity<>(referenceDtos, HttpStatus.OK);
    }

}
