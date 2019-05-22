package com.alinso.myapp.service;

import com.alinso.myapp.entity.Photo;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.PhotoRepository;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    FileStorageUtil fileStorageService;

    @Autowired
    UserService userService;

    @Autowired
    UserEventService userEventService;

    @Autowired
    BlockService  blockService;



    public List<String> savePhotos(MultipartFile[] multipartPhotos) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> photoNames = new ArrayList<>();

        for (MultipartFile file : multipartPhotos) {

            //store photo file
            //String extension =  FilenameUtils.getExtension(file.getOriginalFilename());
            String newName = fileStorageService.makeFileName()+".jpg";

            fileStorageService.storeFile(file, newName,false);
            photoNames.add(newName);

            //save to the database
            Photo photo = new Photo();
            photo.setFileName(newName);
            photo.setUser(user);
            photoRepository.save(photo);
      //      userEventService.newPhotoAdded();

        }

        return photoNames;
    }

    public List<Photo> findByUserId(Long id){
        if(blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");

        User user  =userService.findEntityById(id);
        return photoRepository.findByUser(user);
    }


    public void deletePhoto(String photoName){
        Photo photo = photoRepository.findByFileName(photoName).get();
        UserUtil.checkUserOwner(photo.getUser().getId());

        if(photo!=null){
            fileStorageService.deleteFile(photoName);
            photoRepository.delete(photo);
        //    userEventService.photoDeleted();
        }
    }


}

