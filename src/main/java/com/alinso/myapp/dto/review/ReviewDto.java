package com.alinso.myapp.dto.review;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ReviewType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReviewDto {

    @NotBlank(message = "Referans metni boş olamaz")
    private String reference;

    @NotNull
    private User reader;


    private User writer;

    private ReviewType reviewType;

    @NotNull
    private Boolean isPositive;


    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public void setReviewType(ReviewType reviewType) {
        this.reviewType = reviewType;
    }

    public Boolean getPositive() {
        return isPositive;
    }

    public void setPositive(Boolean positive) {
        isPositive = positive;
    }
}
