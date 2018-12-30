package com.alinso.myapp.util;

import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserUtil {
    public static Integer calculateAge(UserDto userDto){
        Integer ageInt=null;
        if(userDto.getbDateString()!=null) {
            Date birthDate = null;
            Calendar calenderNow = Calendar.getInstance();
            Calendar calenderBirthDate = Calendar.getInstance();
            try {
                birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(userDto.getbDateString());
                calenderBirthDate.setTime(birthDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ageInt = calenderNow.get(Calendar.YEAR) - calenderBirthDate.get(Calendar.YEAR);
        }
        return ageInt;
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
