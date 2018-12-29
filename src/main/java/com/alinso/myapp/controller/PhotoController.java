package com.alinso.myapp.controller;


import com.alinso.myapp.dto.AlbumDto;
import com.alinso.myapp.entity.Photo;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.service.MapValidationErrorService;
import com.alinso.myapp.service.PhotoService;
import com.alinso.myapp.service.UserService;
import com.alinso.myapp.validator.AlbumValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    AlbumValidator albumValidator;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    PhotoService photoService;

    @Autowired
    UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile[] files){

        AlbumDto albumDto =  new AlbumDto();
        albumDto.setFiles(files);

        DataBinder binder = new DataBinder(albumDto);
        binder.setValidator(albumValidator);
        binder.validate();

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(binder.getBindingResult());
        if(errorMap != null) return errorMap;

        List<String> photoNames = photoService.savePhotos(files);

        return new ResponseEntity<List<String>>(photoNames,HttpStatus.ACCEPTED);
    }


    @GetMapping("/album/{id}")
    public ResponseEntity<?> getAlbum(@PathVariable("id") Long id){
            List<Photo> photos  =photoService.findByUserId(id);
            List<String> photoNames  = new ArrayList<>();

            for(Photo photo :photos){
                photoNames.add(photo.getFileName());
            }
        return new ResponseEntity<List<String>>(photoNames,HttpStatus.ACCEPTED);
    }

    @PostMapping("delete")
    public ResponseEntity<?> delete(@RequestBody Map<String,String> photoName){ //send via fileName param

        photoService.deletePhoto(photoName.get("fileName"));

        return new ResponseEntity<String>("deleted",HttpStatus.ACCEPTED);
    }

}
