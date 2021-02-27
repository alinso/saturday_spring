package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.VoteType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Vote extends BaseEntity{

    @ManyToOne
    @NotNull
    private User writer;

    @ManyToOne
    @NotNull
    private User reader;

    @Column
    @NotNull
    private VoteType voteType;


    @Column(columnDefinition = "integer default 0")
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

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
