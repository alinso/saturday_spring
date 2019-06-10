package com.alinso.myapp.controller;

import com.alinso.myapp.entity.InfoPage;
import com.alinso.myapp.service.InfoPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/infoPage")
public class InfoPageController{

    @Autowired
    InfoPageService infoPageService;

    @PostMapping("/update")
    public ResponseEntity<?> save(@RequestBody InfoPage infoPage){
        infoPageService.update(infoPage);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        InfoPage infoPage  =infoPageService.findById(id);
        return new ResponseEntity<InfoPage>(infoPage, HttpStatus.OK);
    }

}
