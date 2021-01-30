package com.alinso.myapp.controller;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.entity.dto.security.ChangePasswordDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.dto.user.RegisterDto;
import com.alinso.myapp.repository.CityRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.security.JwtTokenProvider;
import com.alinso.myapp.security.SecurityConstants;
import com.alinso.myapp.security.payload.JWTLoginSucessReponse;
import com.alinso.myapp.security.payload.LoginRequest;
import com.alinso.myapp.service.UserService;
import com.alinso.myapp.service.VoteService;
import com.alinso.myapp.util.MapValidationErrorUtil;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    RegisterValidator registerValidator;

    @Autowired
    ChangePasswordValidator changePasswordValidator;

    @Autowired
    PhotoValidator profilePicValidator;


    @Autowired
    VoteService voteService;

    @Autowired
    UserUpdateValidator userUpdateValidator;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("ok")
    public ResponseEntity<?> ok(){
        return new ResponseEntity<>("okkey",HttpStatus.OK);
    }



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByPhone(loginRequest.getUsername());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSucessReponse(true, jwt, user.getProfilePicName(),user.getCity().getId()));
    }

    @GetMapping("deleteAccount/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable("userId") Long userId){
        User currentUser  =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(currentUser.getId() == userId){
            userService.deleteById(userId);
        }
        return new ResponseEntity<>("Silindi",HttpStatus.OK);
    }

//    @GetMapping("verifyMobile/{code}")
//    public ResponseEntity<?> verifyCode(@PathVariable("code") Integer code){
//        userService.completeRegistration(code);
//        return new ResponseEntity<String>("verified", HttpStatus.OK);
//    }

//    @GetMapping("userCount")
//    public ResponseEntity<?> userCount(){
//        return new ResponseEntity<>(userService.getUserCount(),HttpStatus.OK);
//    }

//    @GetMapping("/verifyMail/{token}")
//    public ResponseEntity<?> verifyMail(@PathVariable("token") String token) {
//        userService.verifyMail(token);
//        return new ResponseEntity<String>("verified", HttpStatus.OK);
//    }

    @GetMapping("forgottenPassword/{phone}")
    public ResponseEntity<?> sendForgottenPasswordMail(@PathVariable("phone") String phone) {
        userService.forgottePasswordSendPass(phone);
        return new ResponseEntity<>("mail sent", HttpStatus.OK);
    }

//    @PostMapping("/resetPassword")
//    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPassword, BindingResult result) {
//        resetPasswordValidator.validate(resetPassword, result);
//
//        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
//        if (errorMap != null) return errorMap;
//
//        userService.resetPassword(resetPassword);
//        return new ResponseEntity<>("password reset", HttpStatus.OK);
//
//    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto, BindingResult result) {
        // Validate passwords match
        registerValidator.validate(registerDto, result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;
        User newUser = userService.register(registerDto);

        return new ResponseEntity<String>("Created", HttpStatus.CREATED);
    }


    @GetMapping("/getNameForRegistration/{approvalCode}")
    public ResponseEntity<String> getNameForRegistration(@PathVariable("approvalCode") String approvalCode) {
        String name= userService.getNameForRegistration(approvalCode);
        return new ResponseEntity<String>(name, HttpStatus.CREATED);
    }


    @GetMapping("/myProfile")
    public ResponseEntity<?> getMyProfileInfoForUpdate() {

        ProfileInfoForUpdateDto profileInfoForUpdateDto = userService.getMyProfileInfoForUpdate();

        return new ResponseEntity<ProfileInfoForUpdateDto>(profileInfoForUpdateDto, HttpStatus.CREATED);
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<?> update(@Valid @RequestBody ProfileInfoForUpdateDto profileInfoForUpdateDto, BindingResult result) {


        //set logged usre id because of security issues
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        profileInfoForUpdateDto.setId(loggedUser.getId());

        userUpdateValidator.validate(profileInfoForUpdateDto, result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        ProfileInfoForUpdateDto newProfileInfoForUpdateDto = userService.update(profileInfoForUpdateDto);

        return new ResponseEntity<ProfileInfoForUpdateDto>(newProfileInfoForUpdateDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult result) {

        changePasswordValidator.validate(changePasswordDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        userService.changePassword(changePasswordDto);

        return new ResponseEntity<ChangePasswordDto>(changePasswordDto, HttpStatus.ACCEPTED);
    }


    @GetMapping("calculatePercent")
    public ResponseEntity<?> calculatePercent(){
        City city =  cityRepository.findById(Long.valueOf(1)).get();
        List<User> users  =userRepository.findAbovePoint(20,city);
        List<User>  newUsers = new ArrayList<>();

        int i=0;
        for(User u:users){
            i++;

            Integer vote = voteService.calculateVote(u.getId());
            u.setPercent(vote);
            newUsers.add(u);
            if(i%100==0){
                userRepository.saveAll(newUsers);
                newUsers=new ArrayList<>();
            }
        }
        userRepository.saveAll(newUsers);


        return new ResponseEntity<>("ok",HttpStatus.OK);
    }





    @PostMapping("/updateProfilePic")
    public ResponseEntity<?> changeProfilePic(SinglePhotoUploadDto singlePhotoUploadDto, BindingResult result) {

        profilePicValidator.validate(singlePhotoUploadDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        String picName = userService.updateProfilePic(singlePhotoUploadDto);
        return new ResponseEntity<String>(picName, HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        userService.deleteById(id);

        return new ResponseEntity<String>("User has been deleted", HttpStatus.ACCEPTED);
    }

    @GetMapping("search/{searchText}/{pageNum}")
    public ResponseEntity<?> search(@PathVariable("searchText") String searchText,@PathVariable("pageNum") Integer pageNum) {
        List<ProfileDto> profileDtos = userService.searchUser(searchText,pageNum);
        return new ResponseEntity<>(profileDtos, HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> profile(@PathVariable("id") Long id) {
        ProfileDto profileDto = userService.getProfileById(id);

        profileDto.setAttendPercent(userService.attendanceRate(id));
        profileDto.setFollowerCount(userService.followerCount(id));

        //TODO: references and reviews will be loaded too..

        return new ResponseEntity<>(profileDto, HttpStatus.OK);
    }

    @GetMapping("attendanceRate/{id}")
    public ResponseEntity<?> attendanceRate(@PathVariable("id") Long id){
        Integer rate  = userService.attendanceRateOfRequestOwner(id);
        return new ResponseEntity<>(rate,HttpStatus.OK);
    }
    @GetMapping("userAttendanceRate/{id}")
    public ResponseEntity<?> userAttendanceRate(@PathVariable("id") Long id){
        Integer rate  = userService.attendanceRate(id);
        return new ResponseEntity<>(rate,HttpStatus.OK);
    }

}





















