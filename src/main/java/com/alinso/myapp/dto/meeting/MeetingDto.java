package com.alinso.myapp.dto.meeting;

import com.alinso.myapp.dto.user.ProfileDto;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class MeetingDto {
    private Long id;
    private ProfileDto profileDto;

    @NotBlank(message = "Bu kısmı boş bırakamazsınız")
    private String detail;
    private String photoName;
    private MultipartFile file;
    private String updatedAt;
    private List<ProfileDto> attendants;
    private Boolean isThisUserJoins;

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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public Boolean getThisUserJoins() {
        return isThisUserJoins;
    }

    public void setThisUserJoins(Boolean thisUserJoins) {
        isThisUserJoins = thisUserJoins;
    }
}
