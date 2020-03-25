package com.alinso.myapp.controller;


import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.BlockRepository;
import com.alinso.myapp.repository.FollowRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.AdminService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("xbatmany")
@RestController
public class BatmanController  {


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    BlockRepository blockRepository;


    @GetMapping("updatePoint/")
    public ResponseEntity<?> updatePoint() {

        List<User> all = userRepository.findAll();
        List<User> toBeSaved = new ArrayList<>();

        int i = 0;
        for (User u : all) {
            Integer p = userService.calculateUserPoint(u);
            u.setPoint(p);
            toBeSaved.add(u);
            i++;
            if (i % 50 == 0   ||  (i+1)==all.size()) {
                userRepository.saveAll(toBeSaved);
                toBeSaved.clear();
            }
        }
        return new ResponseEntity<String>("guncelendi " + i, HttpStatus.OK);
    }

//    @GetMapping("updateScore/")
//    public ResponseEntity<?> updateScore() {
//
//        List<User> all = userRepository.findAll();
//        List<User> toBeSaved = new ArrayList<>();
//        List<User> maximumFollowerUsers = followRepository.maxFollowedUsers();
//        Integer maximumFollowerCount = followRepository.findFollowerCount(maximumFollowerUsers.get(0));
//        List<User> maximumBlockedUsers = blockRepository.maxBlockedUsers();
//        Integer maximumBlockedCount = blockRepository.blockerCount(maximumBlockedUsers.get(0));
//        int i = 0;
//        for (User u : all) {
//            if(u.getId()==3212){
//                u.setSocialScore(-1);
//                continue;
//            }
//
//            Integer p = userService.calculateSocialScore(u,maximumBlockedCount,maximumFollowerCount);
//            u.setSocialScore(p);
//            toBeSaved.add(u);
//            i++;
//            if (i % 50 == 0   ||  (i+1)==all.size()) {
//                userRepository.saveAll(toBeSaved);
//                toBeSaved.clear();
//            }
//        }
//        return new ResponseEntity<String>("guncelendi " + i, HttpStatus.OK);
//    }

    @GetMapping("updateInvalidUserName/{id}")
    public ResponseEntity<String> updateUSer(@PathVariable("id") Long id) {
        adminService.updateInvalidUsername(id);
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }

    @GetMapping("userInfo/{id}")
    public ResponseEntity<User> userInfo(@PathVariable("id") Long id) {
        User user = adminService.userInfo(id);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @GetMapping("updateExtraPoint/{id}/{extraPoint}")
    public ResponseEntity<String> updateExtraPoint(@PathVariable("id") Long id,@PathVariable("extraPoint") Integer extraPoint) {
        adminService.updateExtraPoint(id,extraPoint);
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }



}
