//package com.alinso.myapp.validator;
//
//import com.alinso.myapp.entity.dto.security.ResetPasswordDto;
//import com.alinso.myapp.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//@Component
//public class ResetPasswordValidator implements Validator {
//    @Autowired
//    UserService userService;
//
//    @Override
//    public boolean supports(Class<?> aClass) {
//        return ResetPasswordDto.class.equals(aClass);
//    }
//
//    @Override
//    public void validate(Object object, Errors errors) {
//
//        ResetPasswordDto changePasswordDto = (ResetPasswordDto) object;
//
//        if (changePasswordDto.getPassword().length() < 6) {
//            errors.rejectValue("password", "Length", "Şifre en az 6 karakter olmalıdır");
//        }
//
//        if (!changePasswordDto.getPassword().equals(changePasswordDto.getPasswordConfirm())) {
//            errors.rejectValue("passwordConfirm", "Match", "Şifreler eşleşmiyor");
//        }
//    }
//
//}
