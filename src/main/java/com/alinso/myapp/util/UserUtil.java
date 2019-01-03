package com.alinso.myapp.util;

import com.alinso.myapp.entity.User;

import java.util.Calendar;

public class UserUtil {
//    public static Integer calculateAge(UserProfileDto userProfileDto){
//        Integer ageInt=null;
//        if(userProfileDto.getbDateString()!=null) {
//            Date birthDate = null;
//            Calendar calenderNow = Calendar.getInstance();
//            Calendar calenderBirthDate = Calendar.getInstance();
//            try {
//                birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(userProfileDto.getbDateString());
//                calenderBirthDate.setTime(birthDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            ageInt = calenderNow.get(Calendar.YEAR) - calenderBirthDate.get(Calendar.YEAR);
//        }
//        return ageInt;
//    }

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
