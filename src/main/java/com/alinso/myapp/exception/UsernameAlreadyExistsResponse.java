package com.alinso.myapp.exception;

public class UsernameAlreadyExistsResponse {

    private String email;

    public UsernameAlreadyExistsResponse(String username) {
        this.email = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
