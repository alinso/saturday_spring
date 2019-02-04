package com.alinso.myapp.util;

import com.alinso.myapp.exception.UserWarningException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class FileStorageUtil {

    private Path fileUploadDir;
    private Path tmpFileUploadDir;

    @Value("${upload.path}")
    private String fileUploadDirString;


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

    public void storeFile(MultipartFile file, String newName,Boolean isProfile) {
        fileUploadDir = Paths.get(fileUploadDirString);
        tmpFileUploadDir = Paths.get(fileUploadDirString+"/tmp");

        try {
            Path tmpFilePath = this.tmpFileUploadDir.resolve(newName);
            Files.copy(file.getInputStream(), tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            saveResizedImage(newName,isProfile);
            deleteTmpFile(tmpFilePath.toString());

        } catch (IOException ex) {
            throw new UserWarningException("Could not store util " + newName + ". Please try again!. IO message: " + ex.getMessage());
        }
    }

    private void saveResizedImage(String name, Boolean isProfile){


        int targetWidth = 700;
        if(isProfile)
            targetWidth=250;


        try {
            File input = new File(fileUploadDirString+"/tmp/"+name);
            BufferedImage image = ImageIO.read(input);

            Integer width = image.getWidth();
            Integer height = image.getHeight();

            //1000 * 3000 -> 500*150
            Double rate  =Double.parseDouble( width.toString()) / targetWidth;
            Double targetHeightDouble = Double.parseDouble(height.toString()) / rate;



            BufferedImage resized = resize(image, targetHeightDouble.intValue(), targetWidth);
            File output = new File(fileUploadDirString+"/"+name);
            ImageIO.write(resized, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    private void deleteTmpFile(String tmpFilePath) {
        File file = new File(tmpFilePath);
        file.delete();
    }

    public void deleteFile(String name) {
        File file = new File(fileUploadDirString+"/"+name);
        file.delete();
    }

    public String saveFileAndReturnName(MultipartFile file) {
        String newName = null;
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            newName = makeFileName() + "." + extension;
            storeFile(file, newName,false);
        }
        return newName;
    }

}



