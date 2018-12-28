package com.alinso.myapp.security.payload;

public class JWTLoginSucessReponse {
    private boolean success;
    private String token;
    private String userName;

    public JWTLoginSucessReponse(boolean success, String token, String userName) {
        this.success = success;
        this.token = token;
        this.userName = userName;
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
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
