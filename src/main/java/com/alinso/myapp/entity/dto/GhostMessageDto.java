package com.alinso.myapp.entity.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class GhostMessageDto {

    private Long id;

    @NotBlank(message = "Mesaj boş olamaz")
    private String message;

    @NotNull
    private Integer delete;

    private String createdAt;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Integer getDelete() {
        return delete;
    }

    public void setDelete(Integer delete) {
        this.delete = delete;
    }
}