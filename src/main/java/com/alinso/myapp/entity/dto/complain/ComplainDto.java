package com.alinso.myapp.entity.dto.complain;

import javax.validation.constraints.NotBlank;

public class ComplainDto {

    @NotBlank(message = "Bu kısım boş olamaz")
    private String detail;
    private Long guiltyId;


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getGuiltyId() {
        return guiltyId;
    }

    public void setGuiltyId(Long guiltyId) {
        this.guiltyId = guiltyId;
    }
}
