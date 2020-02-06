package com.alinso.myapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class ActivityPhoto extends BaseEntity {

    @Column
    @NotNull
    private String fileName;

    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    private Activity activity;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
