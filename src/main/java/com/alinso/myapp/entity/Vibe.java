package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.VibeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Vibe extends BaseEntity{

    @ManyToOne
    @NotNull
    private User writer;

    @ManyToOne
    @NotNull
    private User reader;

    @Column
    @NotNull
    private VibeType vibeType;


    @Column
    private Integer deleted;

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }

    public VibeType getVibeType() {
        return vibeType;
    }

    public void setVibeType(VibeType vibeType) {
        this.vibeType = vibeType;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
