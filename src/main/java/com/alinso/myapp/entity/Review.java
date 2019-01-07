package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.ReviewType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Review extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Referans metni boş olamaz")
    private String reference;

    @ManyToOne
    @NotNull
    private User reader;

    @ManyToOne
    @NotNull
    private User writer;

    @Column
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private ReviewType reviewType;

    @Column
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
