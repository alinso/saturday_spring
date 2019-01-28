package com.alinso.myapp.entity.dto.photo;
import org.springframework.web.multipart.MultipartFile;

public class SinglePhotoUploadDto {

    MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}