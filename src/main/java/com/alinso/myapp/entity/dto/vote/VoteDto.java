package com.alinso.myapp.entity.dto.vote;

import com.alinso.myapp.entity.enums.VoteType;

public class VoteDto {

    private Long writerId;
    private Long readerId;
    private VoteType voteType;

    public Long getWriterId() {
        return writerId;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }
}
