package com.alinso.myapp.service;

import com.alinso.myapp.entity.Photo;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.file.FileStorageService;
import com.alinso.myapp.repository.PhotoRepository;
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
    FileStorageService fileStorageService;

    @Value("${upload.path}")
    private String fileUploadPath;

    public void savePhotos(MultipartFile[] multipartPhotos) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        for (MultipartFile file : multipartPhotos) {

            //store photo file
            String extension =  FilenameUtils.getExtension(file.getOriginalFilename());
            String newName = fileStorageService.makeFileName()+"."+extension;

            fileStorageService.storeFile(file, fileUploadPath, newName);

            //save to the database
            Photo photo = new Photo();
            photo.setFileName(newName);
            photo.setUser(user);
            photoRepository.save(photo);

        }


    }
}

