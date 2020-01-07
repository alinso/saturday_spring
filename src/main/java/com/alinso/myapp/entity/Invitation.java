package com.alinso.myapp.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Invitation extends BaseEntity {


    @ManyToOne
    @NotNull
    private Activity activity;


    @ManyToOne
    @NotNull
    private  User reader;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }
}
