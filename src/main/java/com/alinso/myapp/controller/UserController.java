package com.alinso.myapp.controller;

import com.alinso.myapp.dto.ChangePasswordDto;
import com.alinso.myapp.dto.PhotoDto;
import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.security.JwtTokenProvider;
import com.alinso.myapp.security.SecurityConstants;
import com.alinso.myapp.security.payload.JWTLoginSucessReponse;
import com.alinso.myapp.security.payload.LoginRequest;
import com.alinso.myapp.service.MapValidationErrorService;
import com.alinso.myapp.service.UserService;
import com.alinso.myapp.validator.ChangePasswordValidator;
import com.alinso.myapp.validator.ProfilePicValidator;
import com.alinso.myapp.validator.UserUpdateValidator;
import com.alinso.myapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserValidator userValidator;

    @Autowired
    ChangePasswordValidator changePasswordValidator;

    @Autowired
    ProfilePicValidator profilePicValidator;

    @Autowired
    UserUpdateValidator userUpdateValidator;


    @Autowired
    MapValidationErrorService mapValidationErrorService;

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

       UserDto user =  userService.findByEmail(loginRequest.getUsername());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX +  tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSucessReponse(true, jwt,user.getName()));
    }

    @GetMapping("/verifyMail/{token}")
    public ResponseEntity<?> verifyMail(@PathVariable("token") String token){
        userService.verifyMail(token);
        return new ResponseEntity<String>("verified",HttpStatus.OK);
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result){
        // Validate passwords match
        userValidator.validate(user,result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        user.setPassword(user.getPassword());
        User newUser = userService.register(user);

        return  new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable(value="id") Long id){

        UserDto userDto  = userService.findById(id);

        return  new ResponseEntity<UserDto>(userDto, HttpStatus.CREATED);
    }


    @PostMapping("/updateInfo")
    public ResponseEntity<?> update(@Valid @RequestBody UserDto userDto,BindingResult result){


        //set logged usre id because of security issues
        User loggedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDto.setId(loggedUser.getId());

        userUpdateValidator.validate(userDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        UserDto newUserDto =  userService.update(userDto);

        return new ResponseEntity<UserDto>(newUserDto, HttpStatus.ACCEPTED);
    }


    @PostMapping("/updatePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult result){

        changePasswordValidator.validate(changePasswordDto,result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

         userService.changePassword(changePasswordDto);

        return new ResponseEntity<ChangePasswordDto>(changePasswordDto, HttpStatus.ACCEPTED);
    }



    @PostMapping("/updateProfilePic")
    public ResponseEntity<?> changeProfilePic(PhotoDto photoDto, BindingResult  result){

        profilePicValidator.validate(photoDto,result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if(errorMap != null)return errorMap;

        String picName = userService.updateProfilePic(photoDto);
        return new ResponseEntity<String>(picName, HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value="id") Long id){
        userService.deleteById(id);

        return new ResponseEntity<String>("User has been deleted", HttpStatus.ACCEPTED);
    }

    @GetMapping("search/{searchText}")
    public ResponseEntity<?> search(@PathVariable("searchText") String searchText){
        List<UserDto> userDtoList =  userService.searchUser(searchText);
        return  new ResponseEntity<>(userDtoList,HttpStatus.OK);
    }

}





















