package com.alinso.myapp.controller;

import com.alinso.myapp.dto.ChangePasswordDto;
import com.alinso.myapp.dto.PhotoUploadDto;
import com.alinso.myapp.dto.ResetPasswordDto;
import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.security.JwtTokenProvider;
import com.alinso.myapp.security.SecurityConstants;
import com.alinso.myapp.security.payload.JWTLoginSucessReponse;
import com.alinso.myapp.security.payload.LoginRequest;
import com.alinso.myapp.service.MapValidationErrorService;
import com.alinso.myapp.service.UserService;
import com.alinso.myapp.util.UserUtil;
import com.alinso.myapp.validator.*;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private ResetPasswordValidator resetPasswordValidator;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if (errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDto user = userService.findByEmail(loginRequest.getUsername());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSucessReponse(true, jwt, user.getName()));
    }

    @GetMapping("/verifyMail/{token}")
    public ResponseEntity<?> verifyMail(@PathVariable("token") String token) {
        userService.verifyMail(token);
        return new ResponseEntity<String>("verified", HttpStatus.OK);
    }

    @GetMapping("forgottenPassword/{mail}")
    public ResponseEntity<?> sendForgottenPasswordMail(@PathVariable("mail") String mail) {
        userService.forgottePasswordSendMail(mail);
        return new ResponseEntity<>("mail sent", HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPassword, BindingResult result) {
        resetPasswordValidator.validate(resetPassword, result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if (errorMap != null) return errorMap;

        userService.resetPassword(resetPassword);
        return new ResponseEntity<>("password reset", HttpStatus.OK);

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        // Validate passwords match
        userValidator.validate(user, result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if (errorMap != null) return errorMap;

        user.setPassword(user.getPassword());
        User newUser = userService.register(user);

        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/myProfile")
    public ResponseEntity<?> findById() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDto userDto = userService.findById(user.getId());

        return new ResponseEntity<UserDto>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<?> update(@Valid @RequestBody UserDto userDto, BindingResult result) {


        //set logged usre id because of security issues
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDto.setId(loggedUser.getId());

        userUpdateValidator.validate(userDto, result);
        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if (errorMap != null) return errorMap;

        UserDto newUserDto = userService.update(userDto);

        return new ResponseEntity<UserDto>(newUserDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult result) {

        changePasswordValidator.validate(changePasswordDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if (errorMap != null) return errorMap;

        userService.changePassword(changePasswordDto);

        return new ResponseEntity<ChangePasswordDto>(changePasswordDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/updateProfilePic")
    public ResponseEntity<?> changeProfilePic(PhotoUploadDto photoUploadDto, BindingResult result) {

        profilePicValidator.validate(photoUploadDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
        if (errorMap != null) return errorMap;

        String picName = userService.updateProfilePic(photoUploadDto);
        return new ResponseEntity<String>(picName, HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        userService.deleteById(id);

        return new ResponseEntity<String>("User has been deleted", HttpStatus.ACCEPTED);
    }

    @GetMapping("search/{searchText}")
    public ResponseEntity<?> search(@PathVariable("searchText") String searchText) {
        List<UserDto> userDtoList = userService.searchUser(searchText);
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> profile(@PathVariable("id") Long id) {
        UserDto userDto = userService.findById(id);

        userDto.setAge(UserUtil.calculateAge(userDto));

        //TODO: references and reviews will be loaded too..

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

}





















