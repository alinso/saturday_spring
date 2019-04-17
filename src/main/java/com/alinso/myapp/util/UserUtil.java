package com.alinso.myapp.util;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Calendar;

public class UserUtil {

    public static void checkUserOwner(Long ownerIdOfEntity){
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(ownerIdOfEntity != loggedUser.getId() && !loggedUser.getRole().equals("ROLE_ADMIN"))
            throw new UserWarningException("Bunu yapmaya yetkiniz yok");
    }

    public static Integer calculateAge(User user){
        Integer ageInt=null;
        if(user.getBirthDate()!=null) {
            Calendar calenderNow = Calendar.getInstance();
            Calendar calenderBirthDate = Calendar.getInstance();
            calenderBirthDate.setTime(user.getBirthDate());
            ageInt = calenderNow.get(Calendar.YEAR) - calenderBirthDate.get(Calendar.YEAR);
        }
        return ageInt;
    }
}
