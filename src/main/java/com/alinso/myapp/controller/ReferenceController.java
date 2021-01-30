package com.alinso.myapp.controller;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.ReferenceService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reference")
public class ReferenceController {

    @Autowired
    ReferenceService referenceService;

    @Autowired
    UserRepository userRepository;


//    @GetMapping("/myReferenceCode")
//    public ResponseEntity<?> myReferenceCode(){
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(user.getReferenceCode().equals("") || user.getReferenceCode()==null)
//            user.setReferenceCode(referenceService.makeReferenceCode());
//        userRepository.save(user);
//
//        String referenceCode = user.getReferenceCode();
//
//        return new ResponseEntity<>(referenceCode,HttpStatus.OK);
//    }

    @GetMapping("/myReferences")
    public ResponseEntity<?> myReferences(){
        List<ProfileDto> profileDtos =  referenceService.getMyReferences();
        return new ResponseEntity<>(profileDtos, HttpStatus.OK);
    }

}
