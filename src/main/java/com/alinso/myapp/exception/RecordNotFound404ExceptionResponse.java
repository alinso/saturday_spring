package com.alinso.myapp.exception;

public class RecordNotFound404ExceptionResponse {
    private String RecordNotFound404Message;

    public RecordNotFound404ExceptionResponse(String message){
        this.RecordNotFound404Message=message;
    }

    public String getRecordNotFound404Message() {
        return RecordNotFound404Message;
    }

    public void setRecordNotFound404Message(String recordNotFound404Message) {
        RecordNotFound404Message = recordNotFound404Message;
    }
}
