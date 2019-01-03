package com.alinso.myapp.validator;

import com.alinso.myapp.dto.user.ProfileInfoForUpdateDto;
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
        return ProfileInfoForUpdateDto.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        ProfileInfoForUpdateDto profileInfoForUpdateDto = (ProfileInfoForUpdateDto) object;
        ProfileInfoForUpdateDto profileInfoForUpdateDtoInDbPhone = userService.findByPhone(profileInfoForUpdateDto.getPhone());


        if(profileInfoForUpdateDtoInDbPhone !=null && profileInfoForUpdateDtoInDbPhone.getId().longValue()!= profileInfoForUpdateDto.getId().longValue()){
            errors.rejectValue("phone","Match", "Bu telefon numarası ile kayıt olunmuş");
        }

        ProfileInfoForUpdateDto profileInfoForUpdateDtoInDbEmail = userService.findByEmail(profileInfoForUpdateDto.getEmail());

        if(profileInfoForUpdateDtoInDbEmail !=null && profileInfoForUpdateDto.getId().longValue()!= profileInfoForUpdateDtoInDbEmail.getId().longValue()){
            errors.rejectValue("email","Match", "Bu email adresi ile daha önce kayıt olunmuş");
        }

        if(profileInfoForUpdateDto.getGender()== Gender.UNSELECTED){
            errors.rejectValue("gender","Match", "Cinsiyet Seçiniz");
        }



        //confirmPassword



    }
}
