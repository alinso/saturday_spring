package com.alinso.myapp.entity.dto.review;

import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.ReviewType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReviewDto {

    private Long reviewId;

    @NotBlank(message = "Yorum metni boş olamaz")
    private String review;

    @NotNull
    private ProfileDto reader;


    private ProfileDto writer;

    private ReviewType reviewType;

    @NotNull
    private Boolean isPositive;

    public ProfileDto getReader() {
        return reader;
    }

    public void setReader(ProfileDto reader) {
        this.reader = reader;
    }

    public ProfileDto getWriter() {
        return writer;
    }

    public void setWriter(ProfileDto writer) {
        this.writer = writer;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }
}
