package com.alinso.myapp.entity.dto.vibe;

import com.alinso.myapp.entity.enums.VibeType;

public class VibeDto {

    private Long writerId;
    private Long readerId;
    private VibeType vibeType;

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

    public VibeType getVibeType() {
        return vibeType;
    }

    public void setVibeType(VibeType vibeType) {
        this.vibeType = vibeType;
    }
}
