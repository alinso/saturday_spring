package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class User extends BaseEntity implements UserDetails {



    @NotBlank(message = "İsim boş olamaz")
    @Column
    private String name;

    @NotBlank(message = "Soyisim boş olamaz")
    @Column
    private String surname;

    @Column
    @Email(message="Geçerli bir email adresi giriniz")
    @NotBlank(message="Email adresi boş olamaz")
    private String email;

    @Column
    @NotNull(message="Telefon boş olamaz")
    private Integer phone;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;

    @Transient
    private String confirmPassword;


    @Column
    private String about="";

    @Column
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message="Cinsiyet Seçiniz")
    private Gender gender;

    @Column
    private String motivation="";

    @Column
    @NotBlank(message = "Referansınız olmadan kayıt olamazsınız!")
    private String referenceCode;

    @Column
    private Double rate=0.0;

    @Column
    private Integer eventCount=0;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
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

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }


    //user details methods


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
