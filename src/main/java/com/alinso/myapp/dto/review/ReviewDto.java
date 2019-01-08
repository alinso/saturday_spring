package com.alinso.myapp.dto.review;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ReviewType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReviewDto {

    @NotBlank(message = "Yorum metni boş olamaz")
    private String review;

    @NotNull
    private User reader;


    private User writer;

    private ReviewType reviewType;

    @NotNull
    private Boolean isPositive;


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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
