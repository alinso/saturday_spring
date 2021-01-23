package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventPhoto;
import com.alinso.myapp.entity.dto.photo.AlbumDto;
import com.alinso.myapp.entity.dto.photo.MultiPhotoUploadDto;
import com.alinso.myapp.service.EventPhotoService;
import com.alinso.myapp.service.EventService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.EventPhotoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/eventAlbum")
public class EventAlbumController {

    @Autowired
    EventPhotoValidator eventPhotoValidator;

    @Autowired
    EventPhotoService eventPhotoService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    EventService eventService;

    @PostMapping("/upload/{eventId}")
    public ResponseEntity<?> upload(@RequestParam MultipartFile[] files, @PathVariable("eventId") Long eventId) {

        MultiPhotoUploadDto multiPhotoUploadDto = new MultiPhotoUploadDto();
        multiPhotoUploadDto.setFiles(files);

        DataBinder binder = new DataBinder(multiPhotoUploadDto);
        binder.setValidator(eventPhotoValidator);
        binder.validate();

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(binder.getBindingResult());
        if (errorMap != null) return errorMap;

        List<String> photoNames = eventPhotoService.savePhotos(files, eventId);

        return new ResponseEntity<List<String>>(photoNames, HttpStatus.ACCEPTED);
    }


    @GetMapping("/album/{id}")
    public ResponseEntity<?> getAlbum(@PathVariable("id") Long id) {

        AlbumDto albumDto = new AlbumDto();
        Event event = eventService.findEntityById(id);


        List<EventPhoto> photos = eventPhotoService.findByEventId(id);
        List<String> photoNames = new ArrayList<>();
        for (EventPhoto photo : photos) {
            photoNames.add(photo.getFileName());
        }
        albumDto.setPhotoNames(photoNames);
        albumDto.setFullName(((Long) event.getId()).toString());

        return new ResponseEntity<AlbumDto>(albumDto, HttpStatus.ACCEPTED);
    }


}
