package com.alinso.myapp.entity;

import javax.persistence.Entity;

@Entity
public class Category extends BaseEntity {

    private String name;

    private Integer watcherCount;

    public String getName() {
        return name;
    }

    public Integer getWatcherCount() {
        return watcherCount;
    }

    public void setWatcherCount(Integer watcherCount) {
        this.watcherCount = watcherCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
