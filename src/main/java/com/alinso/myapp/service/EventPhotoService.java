package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventPhoto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.EventPhotoRepository;
import com.alinso.myapp.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventPhotoService {

    @Autowired
    EventRequestService eventRequestService;

    @Autowired
    EventService eventService;

    @Autowired
    FileStorageUtil fileStorageService;

    @Autowired
    EventPhotoRepository eventPhotoRepository;


    public List<String> savePhotos(MultipartFile[] multipartPhotos,Long eventId) {

        Event event = eventService.findEntityById(eventId);

        List<EventPhoto> photosByEvent = eventPhotoRepository.findPhotosByEvent(event);
        if(photosByEvent.size()>50){
            throw new UserWarningException("Bir aktivite albümüne en fazla 50 fotoğraf yüklenebilir");
        }

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date svenDaysAgo = new Date(System.currentTimeMillis() - (8 * DAY_IN_MS));
        if(svenDaysAgo.compareTo(event.getDeadLine()) > 0){
            throw new UserWarningException("Bu aktive 7 günden daha eski, fotoğraf yükleyemezsin");
        }

        if(eventRequestService.isThisUserApprovedAllTimes(event)){

            List<String> photoNames = new ArrayList<>();

            for (MultipartFile file : multipartPhotos) {

                //store photo file
                String newName = fileStorageService.makeFileName()+".jpg";

                fileStorageService.storeFile(file, newName,false);
                photoNames.add(newName);

                //save to the database
                EventPhoto photo = new EventPhoto();
                photo.setFileName(newName);
                photo.setEvent(event);
                eventPhotoRepository.save(photo);
            }
            return photoNames;
        }
        return null;

    }


    public List<EventPhoto> findByEventId(Long id){

        Event event = eventService.findEntityById(id);
        if(eventRequestService.isThisUserApprovedAllTimes(event))
        return eventPhotoRepository.findPhotosByEvent(event);
        else
            return null;
    }


    public void deleteByEvent(Event eventInDb) {
        List<EventPhoto> photosByEvent = eventPhotoRepository.findPhotosByEvent(eventInDb);
        for (EventPhoto a:photosByEvent){
            fileStorageService.deleteFile(a.getFileName());
            eventPhotoRepository.delete(a);
        }
    }
}
