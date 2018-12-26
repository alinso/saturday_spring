package com.alinso.myapp.controller;

import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.security.JwtTokenProvider;
import com.alinso.myapp.security.SecurityConstants;
import com.alinso.myapp.security.payload.JWTLoginSucessReponse;
import com.alinso.myapp.service.MapValidationErrorService;
import com.alinso.myapp.service.UserService;
import com.alinso.myapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import com.alinso.myapp.security.payload.LoginRequest;
import static com.alinso.myapp.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserValidator userValidator;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX +  tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSucessReponse(true, jwt));
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result){
        // Validate passwords match
        userValidator.validate(user,result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User newUser = userService.register(user);

        return  new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable(value="id") Long id){

        UserDto userDto  = userService.findById(id);

        return  new ResponseEntity<UserDto>(userDto, HttpStatus.CREATED);
    }


    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody UserDto userDto,BindingResult result){
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        UserDto newUserDto =  userService.update(userDto);

        return new ResponseEntity<UserDto>(newUserDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value="id") Long id){
        userService.deleteById(id);

        return new ResponseEntity<String>("User has been deleted", HttpStatus.ACCEPTED);
    }

}





















