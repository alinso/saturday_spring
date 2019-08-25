package com.alinso.myapp.entity;

import com.alinso.myapp.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@NamedStoredProcedureQuery(
        name = "delete_user_sp",
        procedureName = "delete_user",
        parameters = {
                @StoredProcedureParameter(name = "userId", mode = ParameterMode.IN, type = Long.class),
        }
)
public class User extends BaseEntity implements UserDetails {


    @Column
    private Boolean enabled = false;

    @NotBlank(message = "İsim boş olamaz")
    @Column
    private String name;

    @NotBlank(message = "Soyisim boş olamaz")
    @Column
    private String surname;

    @Column
    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email adresi boş olamaz")
    private String email;

    @Column
    @NotBlank
    private String phone;

    @Column
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;

    @Transient
    private String confirmPassword;

    @Column(columnDefinition="TEXT")
    private String about = "";

    @Column
    private String profilePicName = "";

    @Column
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Cinsiyet Seçiniz")
    private Gender gender;

    @Column(columnDefinition="TEXT")
    private String motivation = "";

    @Column
    private String referenceCode;

    //reference parent
    @ManyToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User parent;

    @Column
    @ColumnDefault("0")
    private Integer point ;

    @Column
    @ColumnDefault("0")
    private Integer activityCount;

    @Column
    @ColumnDefault("0")
    private Integer photoCount;

    @Column
    @ColumnDefault("0")
    private Integer reviewCount;

    @ManyToOne
    @ColumnDefault("0")
    private City city;

    @Column(columnDefinition = "TEXT")
    private  String firebaseId;

    @Column
    private Integer extraPoint;

    @Column
    private String role;

    public Integer getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(Integer smsCode) {
        this.smsCode = smsCode;
    }

    @Column
    private Integer smsCode;

//
//    @Column
//    private String interests;

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
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

    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    //user details methods


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
        return enabled;
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



    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
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

    public Integer getExtraPoint() {
        return extraPoint;
    }

    public void setExtraPoint(Integer extraPoint) {
        this.extraPoint = extraPoint;
    }
}
