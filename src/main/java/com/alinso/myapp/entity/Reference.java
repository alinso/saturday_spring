package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Entity
public class Reference extends BaseEntity{

    @Column
    @Size(min=4, max=6)
    @NotBlank
    private String referenceCode;

    @ManyToOne
    @NotNull
    private User parent;

    @OneToOne
    private User child;

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public User getChild() {
        return child;
    }

    public void setChild(User child) {
        this.child = child;
    }
}
