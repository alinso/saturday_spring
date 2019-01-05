package com.alinso.myapp.dto.user;

import com.alinso.myapp.entity.enums.Gender;
import org.springframework.stereotype.Component;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;

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

    @NotNull(message = "Cinsiyet Seçiniz")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private String motivation = "";


    @NotNull
    @Min(1)
    @Max(5)
    private Double rate = 1.0;

    @NotNull
    private Integer meetingCount = 0;

//getter setter

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


    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getMeetingCount() {
        return meetingCount;
    }

    public void setMeetingCount(Integer meetingCount) {
        this.meetingCount = meetingCount;
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
}
