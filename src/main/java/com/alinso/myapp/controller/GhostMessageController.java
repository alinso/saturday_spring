package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.service.GhostMessageService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.PhotoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ghostMessage")
public class GhostMessageController {


    @Autowired
    GhostMessageService ghostMessageService;

    @Autowired
    PhotoValidator photoValidator;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @PostMapping("save/")
    public ResponseEntity<?> follow(@RequestBody Map<String, String> message) {

        ghostMessageService.save(message.get("message"));
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }

    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> changeProfilePic(SinglePhotoUploadDto singlePhotoUploadDto, BindingResult result) {

        photoValidator.validate(singlePhotoUploadDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        String picName = ghostMessageService.uploadPhoto(singlePhotoUploadDto);
        return new ResponseEntity<String>(picName, HttpStatus.ACCEPTED);
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {

        List<String> messages = ghostMessageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("toggleNotification")
    ResponseEntity<?> toggleNotification() {
        ghostMessageService.toggleNotification();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("isReceivingNotification")
    ResponseEntity<?> isReceivingNotification() {
       Boolean res =  ghostMessageService.isReceivingNotification();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
