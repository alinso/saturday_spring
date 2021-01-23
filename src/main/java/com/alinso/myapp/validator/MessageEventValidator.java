package com.alinso.myapp.validator;


import com.alinso.myapp.entity.dto.message.MessageEventDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class MessageEventValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return MessageEventDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MessageEventDto messageEventDto = (MessageEventDto) o;

        if(messageEventDto.getMessage().length()>340){
            errors.rejectValue("message","","Maksimum 340 karakter girebilirsin");

        }

    }
}
