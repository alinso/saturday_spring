package com.alinso.myapp.controller;

import com.alinso.myapp.entity.dto.MessageWallDto;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.service.MessageWallService;
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
@RequestMapping("wallMessage")
public class WallMessageController {


    @Autowired
    MessageWallService messageWallService;

    @Autowired
    PhotoValidator photoValidator;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @PostMapping("save/")
    public ResponseEntity<?> follow(@RequestBody Map<String, String> message) {

        messageWallService.save(message.get("message"));
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }

    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> changeProfilePic(SinglePhotoUploadDto singlePhotoUploadDto, BindingResult result) {

        photoValidator.validate(singlePhotoUploadDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        String picName = messageWallService.uploadPhoto(singlePhotoUploadDto);
        return new ResponseEntity<String>(picName, HttpStatus.ACCEPTED);
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {

        List<MessageWallDto> messages = messageWallService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
    @GetMapping("allPC")
    public ResponseEntity<?> getAllPC() {

        List<MessageWallDto> messages = messageWallService.getAllMessagesPC();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("delete/{messageId}")
    ResponseEntity<?> delete(@PathVariable("messageId") Long messageId) {
        messageWallService.deleteMessage(messageId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
