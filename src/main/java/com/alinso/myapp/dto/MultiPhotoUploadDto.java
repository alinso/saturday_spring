package com.alinso.myapp.dto;

import org.springframework.web.multipart.MultipartFile;

public class MultiPhotoUploadDto {
    private MultipartFile[] files;
    private String sizeError;
    private String typeError;

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public String getSizeError() {
        return sizeError;
    }

    public void setSizeError(String sizeError) {
        this.sizeError = sizeError;
    }

    public String getTypeError() {
        return typeError;
    }

    public void setTypeError(String typeError) {
        this.typeError = typeError;
    }
}
