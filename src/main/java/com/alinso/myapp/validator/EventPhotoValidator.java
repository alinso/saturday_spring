package com.alinso.myapp.validator;

import com.alinso.myapp.entity.dto.photo.MultiPhotoUploadDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;


@Component
public class EventPhotoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return MultiPhotoUploadDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {

        MultiPhotoUploadDto album = (MultiPhotoUploadDto) o;

        for(MultipartFile file:album.getFiles()){

            if(!(file.getContentType().toLowerCase().equals("image/jpg")
                    || file.getContentType().toLowerCase().equals("image/jpeg")
                    || file.getContentType().toLowerCase().equals("image/png"))){
                errors.rejectValue("typeError", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsiniz");
                break;
            }
        }

    }

}
