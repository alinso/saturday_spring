package com.alinso.myapp.validator;

import com.alinso.myapp.entity.dto.event.EventDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;


@Component
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        EventDto eventDto = (EventDto) o;


        MultipartFile file = eventDto.getFile();

        if( file!=null &&  !(file.getContentType().toLowerCase().equals("image/jpg")
                || file.getContentType().toLowerCase().equals("image/jpeg")
                || file.getContentType().toLowerCase().equals("image/png"))){
            errors.rejectValue("file", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsiniz");
        }


        if(eventDto.getSelectedInterestIds().size()>5){
            errors.rejectValue("selectedInterestIds","","Bir aktivite en fazla 5 kategoriye sahip olabilir");
        }
        if(eventDto.getSelectedInterestIds().size()<2){
            errors.rejectValue("selectedInterestIds","","Bir aktivitenin en az 2 kategorisi olmalıdır");
        }

        if(file!=null && file.getSize()>2097152){ //2 MB
            errors.rejectValue("file","","Max dosya boyutu 2 MB olabilir");
        }

        if(eventDto.getDetail().length()>500){
            errors.rejectValue("detail","","Maksimum 500 karakter girebilirsiniz");

        }
    }
}
