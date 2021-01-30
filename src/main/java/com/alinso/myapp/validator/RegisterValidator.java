package com.alinso.myapp.validator;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.dto.user.RegisterDto;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.service.ReferenceService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RegisterValidator implements Validator {

    @Autowired
    UserService userService;

    @Autowired
    ReferenceService referenceService;

    @Override
    public boolean supports(Class<?> aClass) {
        return RegisterDto.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        RegisterDto registerDto = (RegisterDto) object;

        if (registerDto.getPassword().length() < 6) {
            errors.rejectValue("password", "Length", "Password must be at 6 characters");
        }
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Passwords don't match");

        }
        if (registerDto.getGender() == Gender.UNSELECTED) {
            errors.rejectValue("gender", "Match", "Please select gender");
        }
    }
}
