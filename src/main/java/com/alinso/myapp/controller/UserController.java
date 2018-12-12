package com.alinso.myapp.controller;

import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.service.MapValidationErrorService;
import com.alinso.myapp.service.UserService;
import com.alinso.myapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserValidator userValidator;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    UserService userService;


    @GetMapping("/ok")
    public ResponseEntity<?> ok(){
        return  new ResponseEntity<String>("ok",HttpStatus.OK);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result){
        // Validate passwords match
        userValidator.validate(user,result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        User newUser = userService.register(user);

        return  new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable(value="id") Long id){

        UserDto userDto  = userService.findById(id);

        return  new ResponseEntity<UserDto>(userDto, HttpStatus.CREATED);
    }


    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody UserDto userDto){
        UserDto newUserDto =  userService.update(userDto);

        return new ResponseEntity<UserDto>(newUserDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value="id") Long id){
        userService.deleteById(id);

        return new ResponseEntity<String>("User has been deleted", HttpStatus.ACCEPTED);
    }

}





















