package com.alinso.myapp.entity.dto.user;

import com.alinso.myapp.entity.enums.Balance;
import com.alinso.myapp.entity.enums.Gender;
import org.springframework.stereotype.Component;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Component
public class ProfileInfoForUpdateDto {

    @NotNull
    private Long id;

    @NotBlank(message = "Telefon boş olamaz")
    private String phone;

    private String profilePicName;

    @NotBlank(message = "İsim boş olamaz")
    private String name;

    @NotBlank(message = "Soyisim boş olamaz")
    private String surname;

    private String about = "";

    private String bDateString;

    private String interests;

    @NotNull(message = "Cinsiyet Seçiniz")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;


    @Enumerated(EnumType.ORDINAL)
    private Balance balance;

    private String motivation = "";

    private Long cityId;

//getter setter

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
