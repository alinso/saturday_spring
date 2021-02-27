package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.Balance;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.UserStatus;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Entity
public class User extends BaseEntity implements UserDetails {


    @NotBlank(message = "İsim boş olamaz")
    @Column
    private String name;

    @NotBlank(message = "Soyisim boş olamaz")
    @Column
    private String surname;

    @Column
    private Integer percent;

    @Column(columnDefinition = "integer default 0")
    private Integer tooNegative;

    @Column
    @NotBlank
    private String phone;

    @Column
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column
    @Temporal(TemporalType.DATE)
    private Date lastLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Interest> interests;

    private String password;

    private String approvalCode;


    @Column(columnDefinition="TEXT")
    private String about = "";

    @Column
    private String profilePicName = "";

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    @Column(columnDefinition="TEXT")
    private String motivation = "";


    @Column(columnDefinition = "integer default 0")
    private Integer point ;


    @Column
    private Balance balance;

    @Column
    private Boolean enabled;

    @ManyToOne
    @ColumnDefault("0")
    private City city;

    @Column(columnDefinition = "TEXT")
    private  String firebaseId;

    @Column(columnDefinition = "integer default 0")
    private Integer extraPercent;

    @Column
    private String role;



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


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone.replaceAll("\\s+","");
    }





    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();

        SimpleGrantedAuthority user = new SimpleGrantedAuthority("ROLE_USER");
        updatedAuthorities.add(user);

        //if it is admin//
        if(this.getRole()!=null && this.getRole().equals("ROLE_ADMIN")){
            SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");
            updatedAuthorities.add(admin);
        }
        return updatedAuthorities;
    }

    @Override
    public String getUsername() {
        return phone;
    }


    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getExtraPercent() {
        return extraPercent;
    }

    public void setExtraPercent(Integer extraPoint) {
        this.extraPercent = extraPoint;
    }

    public Integer getTooNegative() {
        return tooNegative;
    }

    public void setTooNegative(Integer tooNegative) {
        this.tooNegative = tooNegative;
    }


    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }


    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public Set<Interest> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
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
        return enabled;
    }

    public void setEnabled(Boolean enabled){
        this.enabled=enabled;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}

