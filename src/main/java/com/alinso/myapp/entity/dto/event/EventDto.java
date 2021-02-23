package com.alinso.myapp.entity.dto.event;

import com.alinso.myapp.entity.Interest;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

public class EventDto {
    private Long id;
    private ProfileDto profileDto;

    @NotBlank(message = "Bu kısmı boş bırakamazsınız")
    private String detail;
    private String photoName;
    private MultipartFile file;
    private List<ProfileDto> attendants;
    private List<EventRequestDto> requests;
    private Integer thisUserJoined;
    private Boolean isExpired;
    private City city;
    private Boolean isSecret;
    private Long cityId;
    private Set<Interest> interests;
    private Set<Long> selectedInterestIds;
    @NotBlank(message="İleri bir zaman seçmelisiniz(10 dk sonra, 1 saat sonra...)")
    private String deadLineString;
    private Integer vote;
    private Integer myVote;


    public Set<Interest> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
    }

    public Boolean getSecret() {
        return isSecret;
    }

    public void setSecret(Boolean secret) {
        isSecret = secret;
    }

    public Set<Long> getSelectedInterestIds() {
        return selectedInterestIds;
    }

    public void setSelectedInterestIds(Set<Long> selectedInterestIds) {
        this.selectedInterestIds = selectedInterestIds;
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



    public List<EventRequestDto> getRequests() {
        return requests;
    }

    public void setRequests(List<EventRequestDto> requests) {
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

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public Integer getMyVote() {
        return myVote;
    }

    public void setMyVote(Integer myVote) {
        this.myVote = myVote;
    }
}
