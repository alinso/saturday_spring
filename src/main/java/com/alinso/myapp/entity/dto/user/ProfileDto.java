package com.alinso.myapp.entity.dto.user;

import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.stereotype.Component;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
public class ProfileDto {

    @NotNull
    private Long id;


    private String profilePicName;

    @NotBlank(message = "İsim boş olamaz")
    private String name;

    @NotBlank(message = "Soyisim boş olamaz")
    private String surname;

    private String about = "";

    private String bDateString;

    private Integer age;

    private Integer attendPercent;



    private String premiumType;

    @NotNull(message = "Cinsiyet Seçiniz")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private String motivation = "";

    private Integer activityCount;
    private Integer reviewCount;
    private Integer photoCount;
    private Integer point;
    private String interests;
    private String referenceCode;



//getter setter
public String getPremiumType() {
    return premiumType;
}

    public void setPremiumType(String premiumType) {
        this.premiumType = premiumType;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }


    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }



    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }

    public String getbDateString() {
        return bDateString;
    }

    public void setbDateString(String bDateString) {
        this.bDateString = bDateString;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Integer getAttendPercent() {
        return attendPercent;
    }

    public void setAttendPercent(Integer attendPercent) {
        this.attendPercent = attendPercent;
    }
}

