package com.alinso.myapp.dto.meeting;

import com.alinso.myapp.dto.user.ProfileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MeetingDto {
    private Long id;
    private ProfileDto profileDto;

    @NotBlank(message = "Bu kısmı boş bırakamazsınız")
    private String detail;
    private String photoName;
    private MultipartFile file;
    private List<ProfileDto> attendants;
    private List<MeetingRequestDto> requests;
    private Boolean isThisUserJoined;
    private Boolean isExpired
            ;

    @NotBlank(message="İleri bir zaman seçmelisiniz(10 dk sonra, 1 saat sonra...)")
    private String deadLineString;


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

    public Boolean getThisUserJoined() {
        return isThisUserJoined;
    }

    public void setThisUserJoined(Boolean thisUserJoins) {
        isThisUserJoined = thisUserJoins;
    }

    public List<MeetingRequestDto> getRequests() {
        return requests;
    }

    public void setRequests(List<MeetingRequestDto> requests) {
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
}
