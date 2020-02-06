package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityPhoto;
import com.alinso.myapp.entity.dto.photo.AlbumDto;
import com.alinso.myapp.entity.dto.photo.MultiPhotoUploadDto;
import com.alinso.myapp.service.ActivityPhotoService;
import com.alinso.myapp.service.ActivityService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.ActivityPhotoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/activityPhoto")
public class ActivityPhotoController {

    @Autowired
    ActivityPhotoValidator activityPhotoValidator;

    @Autowired
    ActivityPhotoService activityPhotoService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    ActivityService activityService;

    @PostMapping("/upload/{activityId}")
    public ResponseEntity<?> upload(@RequestParam MultipartFile[] files, @PathVariable("activityId") Long activityId) {

        MultiPhotoUploadDto multiPhotoUploadDto = new MultiPhotoUploadDto();
        multiPhotoUploadDto.setFiles(files);

        DataBinder binder = new DataBinder(multiPhotoUploadDto);
        binder.setValidator(activityPhotoValidator);
        binder.validate();

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(binder.getBindingResult());
        if (errorMap != null) return errorMap;

        List<String> photoNames = activityPhotoService.savePhotos(files, activityId);

        return new ResponseEntity<List<String>>(photoNames, HttpStatus.ACCEPTED);
    }


    @GetMapping("/album/{id}")
    public ResponseEntity<?> getAlbum(@PathVariable("id") Long id) {

        AlbumDto albumDto = new AlbumDto();
        Activity activity = activityService.findEntityById(id);


        List<ActivityPhoto> photos = activityPhotoService.findByActivityId(id);
        List<String> photoNames = new ArrayList<>();
        for (ActivityPhoto photo : photos) {
            photoNames.add(photo.getFileName());
        }
        albumDto.setPhotoNames(photoNames);
        albumDto.setFullName(((Long) activity.getId()).toString());

        return new ResponseEntity<AlbumDto>(albumDto, HttpStatus.ACCEPTED);
    }


}
