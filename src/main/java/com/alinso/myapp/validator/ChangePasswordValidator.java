package com.alinso.myapp.validator;

import com.alinso.myapp.entity.dto.security.ChangePasswordDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordValidator implements Validator {
    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ChangePasswordDto.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        ChangePasswordDto passwordDto = (ChangePasswordDto) object;

        if (passwordDto.getNewPassword().length() < 6) {
            errors.rejectValue("newPassword", "Length", "Şifre en az 6 karakter olmalıdır");
        }

        if (!passwordDto.getNewPassword().equals(passwordDto.getNewPasswordConfirm())) {
            errors.rejectValue("newPasswordConfirm", "Match", "Şifreler eşleşmiyor");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!BCrypt.checkpw(passwordDto.getOldPassword(), user.getPassword())) {
            errors.rejectValue("oldPassword", "Match", "Mevcut Şifre Yanlış");
        }

    }

}
