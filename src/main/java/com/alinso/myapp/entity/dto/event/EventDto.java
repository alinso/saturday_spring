package com.alinso.myapp.entity.dto.event;

import org.springframework.web.multipart.MultipartFile;

public class EventDto {

    private Long id;

    private String detail;

    private String title;

    private String dtString;

    private MultipartFile file;

    private String photoName;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDtString() {
        return dtString;
    }

    public void setDtString(String dtString) {
        this.dtString = dtString;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
