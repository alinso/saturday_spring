package com.alinso.myapp.validator;


import com.alinso.myapp.dto.PhotoUploadDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProfilePicValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PhotoUploadDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PhotoUploadDto photoUploadDto = (PhotoUploadDto) target;


        MultipartFile file = photoUploadDto.getFile();

        if (file != null && file.isEmpty()){
            errors.rejectValue("util", "Match","Dosya Seçiniz");
        }

        if(!(file.getContentType().toLowerCase().equals("image/jpg")
                || file.getContentType().toLowerCase().equals("image/jpeg")
                || file.getContentType().toLowerCase().equals("image/png"))){
            errors.rejectValue("util", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsiniz");
        }

        if(file.getSize()>2097152){ //2 MB
            errors.rejectValue("util","","Max dosya boyutu 2 MB olabilir");
        }

    }
}