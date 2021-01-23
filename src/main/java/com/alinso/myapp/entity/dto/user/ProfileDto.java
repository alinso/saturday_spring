package com.alinso.myapp.entity.dto.user;

import com.alinso.myapp.entity.Interest;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.VoteType;
import org.springframework.stereotype.Component;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
    private String title="";

    private String bDateString;

    private Integer age;

    private Integer attendPercent;

    private Set<Interest> interests;

    private VoteType myVote;

    @NotNull(message = "Cinsiyet Seçiniz")
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private String motivation = "";

    private String referenceCode;



//getter setter

    public Set<Interest> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
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


    public VoteType getMyVote() {
        return myVote;
    }

    public void setMyVote(VoteType myVote) {
        this.myVote = myVote;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

