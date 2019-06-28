package com.alinso.myapp.controller;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.FireBaseTokenDto;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("m")
public class MobileAppController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("a/{tokenpackage}")
    public ResponseEntity<?> androidToken(@PathVariable("tokenpackage") String tokenpackage) {

        //token package consist of {userid(replace ..... to / )}----firebase app token
        String part[] = tokenpackage.split("----");
        String pasword = part[0].replace(".....", "/");
        String firebaseToken = part[1];

        System.out.println(pasword + " _________ " + firebaseToken);

        User user = userRepository.getOne(Long.valueOf(pasword));
        user.setFirebaseId(firebaseToken);
        userRepository.save(user);

        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }


    @GetMapping("a/{userId}/{token}")
    public ResponseEntity<?> androidTokenNew(@PathVariable("userId")Long userId, @PathVariable("token") String token) {


        String t1=token.replaceAll("____","[");
        String t2=t1.replaceAll("----","]");

        User user = userRepository.getOne(userId);
        user.setFirebaseId(t2);
        userRepository.save(user);
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @GetMapping("i/{tokenpackage}")
    public ResponseEntity<?> iosToken(@PathVariable("tokenpackage") String tokenpackage) {

        //token package consist of {userid(replace ..... to / )}----firebase app token
        String part[] = tokenpackage.split("----");
        String pasword = part[0].replace(".....", "/");
        String firebaseToken = part[1];

        System.out.println(pasword + " _________ " + firebaseToken);

        User user = userRepository.findByPassword(pasword);
        user.setFirebaseId(firebaseToken);
        userRepository.save(user);

        return new ResponseEntity<String>("ok", HttpStatus.OK);

    }

    @GetMapping("ok")
    public ResponseEntity<?> ok() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = "ksdjafajlsdf" + user.getId();
        result = result.replace("/", ".....");
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

}
