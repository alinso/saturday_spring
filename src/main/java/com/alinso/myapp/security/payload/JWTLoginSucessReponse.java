package com.alinso.myapp.security.payload;

public class JWTLoginSucessReponse {
    private boolean success;
    private String token;
    private String profilePicName;
    private Long cityId;

    public JWTLoginSucessReponse(boolean success, String token, String profilePicName, Long city_id) {
        this.success = success;
        this.token = token;
        this.profilePicName = profilePicName;
        this.cityId =city_id;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JWTLoginSucessReponse{" +
                "success=" + success +
                ", token='" + token + '\'' +
                ", profilePicName='" + profilePicName + '\'' +
                ", cityId='" + cityId + '\'' +
                '}';
    }

    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }
}
