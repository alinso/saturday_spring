package com.alinso.myapp.controller;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.service.AdminService;
import com.alinso.myapp.service.DiscoverService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("sdklsdf")
@RestController
public class AdminController {

    @Autowired
    DiscoverService discoverService;

    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;

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
    @GetMapping("deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id){
        userService.deleteById(id);
        return new ResponseEntity<>("okk",HttpStatus.OK);

    }
    @GetMapping("updateUser/{id}")
    public ResponseEntity<String> updateUSer(@PathVariable("id") Long id){
        adminService.updateInvalidUsername(id);
        return new ResponseEntity<>("okk",HttpStatus.OK);

    }

    @GetMapping("userInfo/{id}")
    public ResponseEntity<User> userInfo(@PathVariable("id") Long id){
        User user  =adminService.userInfo(id);
        return new ResponseEntity<>(user,HttpStatus.OK);

    }


}