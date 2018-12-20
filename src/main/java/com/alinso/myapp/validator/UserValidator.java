package com.alinso.myapp.validator;

import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        User user = (User) object;

        if(user.getPassword().length() <6){
            errors.rejectValue("password","Length", "Şifre en az 6 karakter olmalıdır");
        }

        if(!user.getPassword().equals(user.getConfirmPassword())){
            errors.rejectValue("confirmPassword","Match", "Şifreler eşleşmiyor");

        }

        UserDto userDtoInDbPhone  = userService.findByPhone(user.getPhone());

        if(userDtoInDbPhone!=null){
            errors.rejectValue("phoneNotUnique","Match", "Bu telefon numarası ile kayıt olunmuş");
        }

        UserDto userDtoInDbEmail  = userService.findByEmail(user.getEmail());

        if(userDtoInDbEmail!=null){
            errors.rejectValue("phoneNotUnique","Match", "Bu email ile kayıt olunmuş");
        }



        //confirmPassword



    }
}
