package com.alinso.myapp.security.payload;

public class JWTLoginSucessReponse {
    private boolean success;
    private String token;
    private String userFullName;
    private Long cityId;

    public JWTLoginSucessReponse(boolean success, String token, String userFullName, Long city_id) {
        this.success = success;
        this.token = token;
        this.userFullName = userFullName;
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
                ", userFullName='" + userFullName + '\'' +
                ", cityId='" + cityId + '\'' +
                '}';
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
