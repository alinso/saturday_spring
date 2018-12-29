package com.alinso.myapp.exception;
public class UserWarningExceptionResponse {

    private String userWarningMessage;

    public UserWarningExceptionResponse(String userNotFound) {
        this.userWarningMessage = userNotFound;
    }

    public String getUserWarningMessage() {
        return userWarningMessage;
    }

    public void setUserWarningMessage(String userWarningMessage) {
        this.userWarningMessage = userWarningMessage;
    }
}