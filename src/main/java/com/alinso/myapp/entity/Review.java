package com.alinso.myapp.entity;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Review extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Yorum metni boş olamaz")
    private String review;

    @ManyToOne
    @NotNull
    private User reader;

    @ManyToOne
    @NotNull
    private User writer;



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

}
