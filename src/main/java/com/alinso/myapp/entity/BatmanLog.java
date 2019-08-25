package com.alinso.myapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class BatmanLog extends BaseEntity {


    @Column
    private Integer point;

    @Column
    private Integer oldPoint;

    @OneToOne
    private User user;


    @OneToOne
    User batman;

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getBatman() {
        return batman;
    }

    public void setBatman(User batman) {
        this.batman = batman;
    }

    public Integer getOldPoint() {
        return oldPoint;
    }

    public void setOldPoint(Integer oldPoint) {
        this.oldPoint = oldPoint;
    }
}
