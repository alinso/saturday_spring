package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class EventComment extends BaseEntity {

    @Column
    @NotBlank
    private String comment;

    @ManyToOne
    @NotNull
    private Event event;

    @Column
    @Min(0)
    @Max(5)
    @NotNull
    private Integer rate;

    @ManyToOne
    @NotNull
    private User writer;

    @ManyToOne
    @NotNull
    private User reader;


}
