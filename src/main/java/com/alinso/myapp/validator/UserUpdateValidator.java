package com.alinso.myapp.validator;

import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
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

        if(profileInfoForUpdateDto.getGender()== Gender.UNSELECTED){
            errors.rejectValue("gender","Match", "Cinsiyet Seçmelisin");
        }
        if(profileInfoForUpdateDto.getCityId()==null){
            errors.rejectValue("cityId","Match", "Şehir Seçmelisin");
        }



        //confirmPassword



    }
}
