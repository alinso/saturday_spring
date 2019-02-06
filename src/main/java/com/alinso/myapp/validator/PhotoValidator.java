package com.alinso.myapp.validator;


import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SinglePhotoUploadDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SinglePhotoUploadDto singlePhotoUploadDto = (SinglePhotoUploadDto) target;


        MultipartFile file = singlePhotoUploadDto.getFile();

        if (file != null && file.isEmpty()){
            errors.rejectValue("file", "Match","Dosya Seçiniz");
        }

        if(!(file.getContentType().toLowerCase().equals("image/jpg")
                || file.getContentType().toLowerCase().equals("image/jpeg")
                || file.getContentType().toLowerCase().equals("image/png"))){
            errors.rejectValue("file", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsiniz");
        }

        if(file.getSize()>1037152){ //2 MB
            errors.rejectValue("file","","Max dosya boyutu 1 MB olabilir");
        }

    }
}