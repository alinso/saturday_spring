package com.alinso.myapp.validator;

import com.alinso.myapp.entity.Application;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.ReferenceService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.jws.soap.SOAPBinding;

@Component
public class ApplicationValidator implements Validator {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReferenceService referenceService;


    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        Application application = (Application) object;
        User user = userRepository.findByPhone(application.getPhone());

        if (application.getPhone().length() != 10) {
            errors.rejectValue("phone", "Match", "Phone number has to be 10 digits! ex: 5535919925");
        }

        if (user != null) {
            errors.rejectValue("phone", "Match", "This phone number is used");
        }

        if(!application.getReferenceCode().equals("")) {
            if(!referenceService.isReferenceCodeValid(application.getReferenceCode())){
                errors.rejectValue("referenceCode", "Match", "Invalid Reference Code");
            }
        }
    }
}
