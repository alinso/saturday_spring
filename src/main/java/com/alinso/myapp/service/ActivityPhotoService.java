package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityPhoto;
import com.alinso.myapp.entity.Photo;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityPhotoRepository;
import com.alinso.myapp.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivityPhotoService {

    @Autowired
    ActivityRequestService activityRequestService;

    @Autowired
    ActivityService activityService;

    @Autowired
    FileStorageUtil fileStorageService;

    @Autowired
    ActivityPhotoRepository activityPhotoRepository;


    public List<String> savePhotos(MultipartFile[] multipartPhotos,Long activityId) {

        Activity activity=activityService.findEntityById(activityId);

        List<ActivityPhoto> activityPhotos = activityPhotoRepository.findActivityPhotosByActivity(activity);
        if(activityPhotos.size()>50){
            throw new UserWarningException("Bir aktivite albümüne en fazla 50 fotoğraf yüklenebilir");
        }

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date svenDaysAgo = new Date(System.currentTimeMillis() - (8 * DAY_IN_MS));
        if(svenDaysAgo.compareTo(activity.getDeadLine()) > 0){
            throw new UserWarningException("Bu aktive 7 günden daha eski, fotoğraf yükleyemezsin");
        }

        if(activityRequestService.isThisUserApprovedAllTimes(activity)){

            List<String> photoNames = new ArrayList<>();


            for (MultipartFile file : multipartPhotos) {

                //store photo file
                String newName = fileStorageService.makeFileName()+".jpg";

                fileStorageService.storeFile(file, newName,false);
                photoNames.add(newName);

                //save to the database
                ActivityPhoto photo = new ActivityPhoto();
                photo.setFileName(newName);
                photo.setActivity(activity);
                activityPhotoRepository.save(photo);
            }
            return photoNames;
        }
        return null;

    }


    public List<ActivityPhoto> findByActivityId(Long id){

        Activity activity  =activityService.findEntityById(id);
        if(activityRequestService.isThisUserApprovedAllTimes(activity))
        return activityPhotoRepository.findActivityPhotosByActivity(activity);
        else
            return null;
    }


    public void deleteByActivity(Activity activityInDb) {
        List<ActivityPhoto> activityPhotos = activityPhotoRepository.findActivityPhotosByActivity(activityInDb);
        for (ActivityPhoto a:activityPhotos){
            fileStorageService.deleteFile(a.getFileName());
            activityPhotoRepository.delete(a);
        }
    }
}
