package com.alinso.myapp.controller;

import com.alinso.myapp.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("statistics")
public class StatisticsController {

    @Autowired
    StatisticsService statistics;

    @GetMapping("aasFemale")
    public ResponseEntity<?> aasFemale(){
        Integer aasFemale  =statistics.aasFemale();

        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }

    @GetMapping("aasMale")
    public ResponseEntity<?> aasMale(){
        Integer aasFemale  =statistics.aasMale();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }



    @GetMapping("maleCount")
    public ResponseEntity<?> maleCount(){
        Integer aasFemale  =statistics.maleCount();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }

    @GetMapping("femaleCount")
    public ResponseEntity<?> femaleCount(){
        Integer aasFemale  =statistics.femaleCount();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }

    @GetMapping("activeFemaleCount")
    public ResponseEntity<?> activeFemaleCount(){
        Integer aasFemale  =statistics.activeFemaleCount();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }
    @GetMapping("activeMaleCount")
    public ResponseEntity<?> activeMaleCount(){
        Integer aasFemale  =statistics.activeMaleCount();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }
    @GetMapping("tooActiveMaleCount")
    public ResponseEntity<?> tooActiveMaleCount(){
        Integer aasFemale  =statistics.tooActiveMaleCount();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }
    @GetMapping("tooActiveFemaleCount")
    public ResponseEntity<?> tooActiveFemaleCount(){
        Integer aasFemale  =statistics.tooActiveFemaleCount();
        return new ResponseEntity<String>(aasFemale.toString(),HttpStatus.OK);
    }


    @GetMapping("registeredWomen")
    public ResponseEntity<?> registeredWomen(){
       List<Integer> womenList=  statistics.newWomenThreeMonths();
        return new ResponseEntity<List<Integer>>(womenList,HttpStatus.OK);

    }
}
