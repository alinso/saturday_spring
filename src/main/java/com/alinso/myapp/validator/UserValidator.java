package com.alinso.myapp.validator;

import com.alinso.myapp.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.service.ReferenceService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    UserService userService;

    @Autowired
    ReferenceService referenceService;

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

        ProfileInfoForUpdateDto profileInfoForUpdateDtoInDbPhone = userService.findByPhone(user.getPhone());

        if(profileInfoForUpdateDtoInDbPhone !=null){
            errors.rejectValue("phone","Match", "Bu telefon numarası ile kayıt olunmuş");
        }

        ProfileInfoForUpdateDto profileInfoForUpdateDtoInDbEmail = userService.findByEmail(user.getEmail());

        if(profileInfoForUpdateDtoInDbEmail !=null){
            errors.rejectValue("email","Match", "Bu email adresi ile daha önce kayıt olunmuş");
        }

        if(user.getGender()== Gender.UNSELECTED){
            errors.rejectValue("gender","Match", "Cinsiyet Seçiniz");
        }

        User parent  = referenceService.findByCode(user.getReferenceCode());
        if(parent==null && !user.getReferenceCode().equals("")){
            errors.rejectValue("referenceCode","Match", "Geçersiz Referans Kodu");
        }



        //confirmPassword



    }
}
