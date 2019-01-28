package com.alinso.myapp.validator;

import com.alinso.myapp.entity.dto.activity.ActivityDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;


@Component
public class ActivityValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ActivityDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ActivityDto activityDto = (ActivityDto) o;


        MultipartFile file = activityDto.getFile();

        if( file!=null &&  !(file.getContentType().toLowerCase().equals("image/jpg")
                || file.getContentType().toLowerCase().equals("image/jpeg")
                || file.getContentType().toLowerCase().equals("image/png"))){
            errors.rejectValue("file", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsiniz");
        }


        if(file!=null && file.getSize()>2097152){ //2 MB
            errors.rejectValue("file","","Max dosya boyutu 2 MB olabilir");
        }

        if(activityDto.getDetail().length()>340){
            errors.rejectValue("detail","","Maksimum 340 karakter girebilirsiniz");

        }


    }
}
