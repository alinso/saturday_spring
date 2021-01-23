//package com.alinso.myapp.entity;
//
//import com.alinso.myapp.entity.enums.PremiumDuration;
//import com.alinso.myapp.entity.enums.PremiumType;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//import java.util.Date;
//
//@Entity
//public class Premium extends BaseEntity {
//    @ManyToOne
//    private User user;
//
//    @Column
//    @Enumerated(EnumType.ORDINAL)
//    @NotNull
//    private PremiumDuration duration;
//
//    @Column
//    @Enumerated(EnumType.ORDINAL)
//    @NotNull
//    private PremiumType type;
//
//    @Column
//    private Date startDate;
//
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public PremiumDuration getDuration() {
//        return duration;
//    }
//
//    public void setDuration(PremiumDuration duration) {
//        this.duration = duration;
//    }
//
//    public PremiumType getType() {
//        return type;
//    }
//
//    public void setType(PremiumType type) {
//        this.type = type;
//    }
//
//    public Date getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(Date startDate) {
//        this.startDate = startDate;
//    }
//}
