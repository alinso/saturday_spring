package com.alinso.myapp.validator;


import com.alinso.myapp.dto.ProfilePicDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class ProfilePicValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ProfilePicDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfilePicDto profilePicDto = (ProfilePicDto) target;


        MultipartFile file = profilePicDto.getFile();

        if (file != null && file.isEmpty()){
            errors.rejectValue("file", "Match","Dosya Seçiniz");
        }

        if(!(file.getContentType().toLowerCase().equals("image/jpg")
                || file.getContentType().toLowerCase().equals("image/jpeg")
                || file.getContentType().toLowerCase().equals("image/png"))){
            errors.rejectValue("file", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsiniz");
        }

        if(file.getSize()>2097152){ //2 MB
            errors.rejectValue("file","","Max dosya boyutu 2 MB olabilir");
        }

    }
}