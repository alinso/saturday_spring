package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.enums.PremiumType;
import com.alinso.myapp.service.DayActionService;
import com.alinso.myapp.service.PremiumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("premium")
public class PremiumController {

    @Autowired
    PremiumService premiumService;

    @Autowired
    DayActionService dayActionService;

    @PostMapping("save")
    public ResponseEntity<?> save(@RequestBody Premium premium){
        premium.setType(PremiumType.SOLD);
        premiumService.save(premium);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("checkActivityLimit")
    public ResponseEntity<?> checkActivityLimit(){
        dayActionService.checkActivityLimit();
        return new ResponseEntity<>("OK",HttpStatus.OK);
    }

    @GetMapping("checkRequestLimit")
    public ResponseEntity<?> checkRequestLimit(){
        dayActionService.checkRequestLimit();
        return new ResponseEntity<>("OK",HttpStatus.OK);
    }
}
