package com.alinso.myapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecordNotFound404Exception extends RuntimeException {
    public RecordNotFound404Exception(String message){
        super(message);
    }
}
