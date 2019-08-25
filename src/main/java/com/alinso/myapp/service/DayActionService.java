package com.alinso.myapp.service;

import com.alinso.myapp.entity.DayAction;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.DayActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DayActionService {


    private final Integer FEMALE_REQUEST_LIMIT = 6;


    private final Integer NEW_MALE_REQUEST_LIMIT = 2;
    private final Integer REQUEST_LIMIT = 3;
    private final Integer OLD_MALE_REQUEST_LIMIT = 4;



    private final Integer FEMALE_ACTIVITY_LIMIT = 4;

    private final Integer NEW_MALE_ACTIVITY_LIMIT = 1;
    private final Integer ACTIVITY_LIMIT = 2;


    @Autowired
    DayActionRepository dayActionRepository;

    @Autowired
    PremiumService premiumService;

    private DayAction getDayAction() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayActionInDb = dayActionRepository.findByUser(user);

        DayAction newDayAction;
        if (dayActionInDb == null) {
            newDayAction = new DayAction();
            newDayAction.setUser(user);
            newDayAction.setRequestCount(0);
            newDayAction.setActivityCount(0);
        } else {
            newDayAction = dayActionInDb;
        }
        return newDayAction;
    }

    public void addRequest() {
        DayAction newDayAction = getDayAction();
        newDayAction.setRequestCount(newDayAction.getRequestCount() + 1);
        dayActionRepository.save(newDayAction);
    }

    public void addActivity() {
        DayAction newDayAction = getDayAction();
        newDayAction.setActivityCount(newDayAction.getActivityCount() + 1);
        dayActionRepository.save(newDayAction);
    }


    public void removeRequest() {
//        DayAction newDayAction = getDayAction();
//        newDayAction.setRequestCount(newDayAction.getRequestCount() - 1);
//        dayActionRepository.save(newDayAction);
    }

    public void removeActivity() {
//        DayAction newDayAction = getDayAction();
//        newDayAction.setActivityCount(newDayAction.getActivityCount() - 1);
//        dayActionRepository.save(newDayAction);
    }

    public void checkActivityLimit() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayAction = dayActionRepository.findByUser(user);

        Integer limit = ACTIVITY_LIMIT;
        String warning  ="";
        if (user.getGender() == Gender.FEMALE && user.getPoint() < 100) {
            limit = FEMALE_ACTIVITY_LIMIT;
            warning = "Haftada en fazla " + limit + " aktivite açabilirsin!";
        }
        if (user.getGender() == Gender.MALE && user.getPoint() < 30) {
            limit = NEW_MALE_ACTIVITY_LIMIT;
            warning = "30 puan altı olduğun için haftada en fazla " + limit + " aktivite açabilirsin!";
        }
        if (user.getGender() == Gender.MALE && user.getPoint() >= 30) {
            limit = ACTIVITY_LIMIT;
            warning = "Haftada en fazla " + limit + " aktivite açabilirsin!";
        }



        if (dayAction != null)
            if (dayAction.getActivityCount() >= limit) {
                throw new UserWarningException(warning);
            }

//        if(!premiumService.isUserPremium(user) && dayAction.getActivityCount() == ACTIVITY_LIMIT){
//            throw new UserWarningException("Haftada en fazla "+ACTIVITY_LIMIT+" aktivite açabilirsin!");
//        }

    }

    public void checkRequestLimit() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayAction = dayActionRepository.findByUser(user);


        Integer limit = REQUEST_LIMIT;
        String warning = "";
        if (user.getGender() == Gender.FEMALE && user.getPoint() < 100) {
            limit = FEMALE_REQUEST_LIMIT;
            warning="Günde en fazla " + limit + " istek gönderebilirsin!";
        }

        if (user.getGender() == Gender.MALE && user.getPoint() < 50) {
            limit = NEW_MALE_REQUEST_LIMIT;
            warning=" 50 puan altı olduğun için günde en fazla " + limit + " istek gönderebilirsin!";
        }
        if (user.getGender() == Gender.MALE && user.getPoint() < 150 && user.getPoint() > 50) {
            limit = REQUEST_LIMIT;
            warning=" 150 puan altı olduğun için günde en fazla " + limit + " istek gönderebilirsin!";
        }
        if (user.getGender() == Gender.MALE && user.getPoint() > 150) {
            limit = OLD_MALE_REQUEST_LIMIT;
            warning="Günde en fazla " + limit + " istek gönderebilirsin!";
        }


        if (dayAction != null)
            if (dayAction.getRequestCount() >= limit) {
                throw new UserWarningException(warning);
            }

        //        if(!premiumService.isUserPremium(user) && (dayAction.getRequestCount() == REQUEST_LIMIT){
//            throw new UserWarningException("Günde en fazla "+REQUEST_LIMIT+" istek gönderebilirsin!");
//        }

    }


    @Scheduled(cron = "0 0 1 * * MON")
    private void clearActivity() {
        dayActionRepository.clearActivity();
    }

    @Scheduled(cron = "0 1 1 * * ?")
    private void clearRequest() {
        dayActionRepository.clearRequest();
    }


}









