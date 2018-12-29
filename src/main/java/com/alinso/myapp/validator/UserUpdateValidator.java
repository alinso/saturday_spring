package com.alinso.myapp.validator;

import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserUpdateValidator implements Validator {

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserDto.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        UserDto userDto = (UserDto) object;
        UserDto userDtoInDbPhone  = userService.findByPhone(userDto.getPhone());


        if(userDtoInDbPhone!=null && userDtoInDbPhone.getId().longValue()!=userDto.getId().longValue()){
            errors.rejectValue("phone","Match", "Bu telefon numarası ile kayıt olunmuş");
        }

        UserDto userDtoInDbEmail  = userService.findByEmail(userDto.getEmail());

        if(userDtoInDbEmail!=null && userDto.getId().longValue()!=userDtoInDbEmail.getId().longValue()){
            errors.rejectValue("email","Match", "Bu email adresi ile daha önce kayıt olunmuş");
        }

        if(userDto.getGender()== Gender.UNSELECTED){
            errors.rejectValue("gender","Match", "Cinsiyet Seçiniz");
        }



        //confirmPassword



    }
}
