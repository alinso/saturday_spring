package com.alinso.myapp.entity.dto.activity;

import com.alinso.myapp.entity.Category;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

public class ActivityDto {
    private Long id;
    private ProfileDto profileDto;

    @NotBlank(message = "Bu kısmı boş bırakamazsınız")
    private String detail;
    private String photoName;
    private MultipartFile file;
    private List<ProfileDto> attendants;
    private List<ActivityRequestDto> requests;
    private Integer thisUserJoined;
    private Boolean isExpired;
    private City city;
    private Boolean isSecret;
    private Long cityId;
    private Set<Category> categories;
    private Set<Long> selectedCategoryIds;

    @NotBlank(message="İleri bir zaman seçmelisiniz(10 dk sonra, 1 saat sonra...)")
    private String deadLineString;



    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Boolean getSecret() {
        return isSecret;
    }

    public void setSecret(Boolean secret) {
        isSecret = secret;
    }

    public Set<Long> getSelectedCategoryIds() {
        return selectedCategoryIds;
    }

    public void setSelectedCategoryIds(Set<Long> selectedCategoryIds) {
        this.selectedCategoryIds = selectedCategoryIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfileDto getProfileDto() {
        return profileDto;
    }

    public void setProfileDto(ProfileDto profileDto) {
        this.profileDto = profileDto;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public List<ProfileDto> getAttendants() {
        return attendants;
    }

    public void setAttendants(List<ProfileDto> attendants) {
        this.attendants = attendants;
    }



    public List<ActivityRequestDto> getRequests() {
        return requests;
    }

    public void setRequests(List<ActivityRequestDto> requests) {
        this.requests = requests;
    }

    public String getDeadLineString() {
        return deadLineString;
    }

    public void setDeadLineString(String deadLineString) {
        this.deadLineString = deadLineString;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }


    public Integer getThisUserJoined() {
        return thisUserJoined;
    }

    public void setThisUserJoined(Integer thisUserJoined) {
        this.thisUserJoined = thisUserJoined;
    }
}
