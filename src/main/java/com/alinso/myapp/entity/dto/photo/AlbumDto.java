package com.alinso.myapp.entity.dto.photo;

import java.util.List;

public class AlbumDto {
    private String fullName;
    private List<String> photoNames;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getPhotoNames() {
        return photoNames;
    }

    public void setPhotoNames(List<String> photoNames) {
        this.photoNames = photoNames;
    }
}
