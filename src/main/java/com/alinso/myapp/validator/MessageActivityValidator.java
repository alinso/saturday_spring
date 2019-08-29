package com.alinso.myapp.validator;


import com.alinso.myapp.entity.dto.message.MessageActivityDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class MessageActivityValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return MessageActivityDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MessageActivityDto activityDto = (MessageActivityDto) o;

        if(activityDto.getMessage().length()>340){
            errors.rejectValue("message","","Maksimum 340 karakter girebilirsin");

        }

    }
}
