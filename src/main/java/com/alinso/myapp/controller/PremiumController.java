package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.PremiumType;
import com.alinso.myapp.service.DayActionService;
import com.alinso.myapp.service.PremiumService;
import com.alinso.myapp.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/premium")
public class PremiumController {

    @Autowired
    PremiumService premiumService;

    @Autowired
    DayActionService dayActionService;

    @PostMapping("save/{userId}")
    public ResponseEntity<?> save(@RequestBody Premium premium, @PathVariable("userId") Long userId) {
        premium.setType(PremiumType.SOLD);
        premiumService.save(premium,userId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("checkActivityLimit")
    public ResponseEntity<?> checkActivityLimit() {
        dayActionService.checkActivityLimit();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

//    @GetMapping("checkRequestLimit")
//    public ResponseEntity<?> checkRequestLimit() {
//        dayActionService.checkRequestLimit();
//        return new ResponseEntity<>("OK", HttpStatus.OK);
//    }


    @GetMapping("latestPremiumDate")
    public ResponseEntity<?> latestPremiumDate() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String latestPremiumDate = DateUtil.dateToString(premiumService.getPremiumLastDate(user), "dd/MM/YYYY");
        return new ResponseEntity<String>(latestPremiumDate, HttpStatus.OK);
    }
}
