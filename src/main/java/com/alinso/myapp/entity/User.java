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
import java.util.*;

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
    private Integer percent;

    @Column
    private String nick;

    @Column
    private String title;//profsyonel kullanıcılar iin tanımlayıcı

    @Column
    private Integer trialUser;

    @Column
    @Email(message = "Geçerli bir email adresi giriniz")
    @NotBlank(message = "Email adresi boş olamaz")
    private String email;

    @Column
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

    public Integer getTrialUser() {
        return trialUser;
    }

    public void setTrialUser(Integer trialUser) {
        this.trialUser = trialUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
}
