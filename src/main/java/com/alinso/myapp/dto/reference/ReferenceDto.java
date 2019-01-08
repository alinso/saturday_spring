package com.alinso.myapp.dto.reference;

import com.alinso.myapp.dto.user.ProfileDto;

public class ReferenceDto {

    private String referenceCode;

    private ProfileDto child;

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public ProfileDto getChild() {
        return child;
    }

    public void setChild(ProfileDto child) {
        this.child = child;
    }
}
