package com.alinso.myapp.entity;

import javax.persistence.Entity;

@Entity
public class Invitation extends BaseEntity {

    private Activity activity;
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
