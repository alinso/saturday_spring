package com.alinso.myapp.exception;

public class InvalidLoginResponse {
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String errorMessage;

    public InvalidLoginResponse() {
        this.errorMessage = "Kullanıcı adı veya şifre yanlış";
    }


}
