package com.alinso.myapp.util;

import com.alinso.myapp.exception.UserWarningException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class FileStorageUtil {

    private Path fileStorageLocation;

    @Autowired
    public FileStorageUtil() {
    }


    public String makeFileName() {
        Character[] characterArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'y', 'z',
                '_', '-', '1', '2', '3', '4', '5', '6', '8', '9', '0'};

        Random rnd = new Random();
        Character c1 = characterArray[rnd.nextInt(33)];
        Character c2 = characterArray[rnd.nextInt(33)];
        Character c3 = characterArray[rnd.nextInt(33)];
        Character c4 = characterArray[rnd.nextInt(33)];
        Character c5 = characterArray[rnd.nextInt(33)];
        Character c6 = characterArray[rnd.nextInt(33)];
        Character c7 = characterArray[rnd.nextInt(33)];
        Character c8 = characterArray[rnd.nextInt(33)];
        Character c9 = characterArray[rnd.nextInt(33)];

        String newName = c1.toString() + c2.toString() + c4.toString() + c3.toString() + c5.toString() + c6.toString() + c7.toString() + c8.toString() + c9.toString();
        return newName;
    }

    public void storeFile(MultipartFile file, String profilePicPath, String newName) {
        fileStorageLocation = Paths.get(profilePicPath);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(newName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            throw new UserWarningException("Could not store util " + newName + ". Please try again!. IO message: " + ex.getMessage());
        }
    }

    public void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    public String saveFileAndReturnName(MultipartFile file, String fileUploadPath) {
        String newName = null;
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            newName = makeFileName() + "." + extension;
            storeFile(file, fileUploadPath, newName);
        }
        return newName;
    }

}



