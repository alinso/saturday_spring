package com.alinso.myapp.service;

import com.alinso.myapp.entity.DayAction;
import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.DayActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DayActionService {


    private Integer NEW_USER_REQUEST_LIMIT=2;
    private Integer OLD_USER_REQUEST_LIMIT=3;
    private Integer SILVER_USER_REQUEST_LIMIT=7;
    private Integer GOLD_USER_REQUEST_LIMIT=20;


    private Integer NEW_USER_ACTIVITY_LIMIT=1;
    private Integer OLD_USER_ACTIVITY_LIMIT=2;
    private Integer SILVER_USER_ACTIVITY_LIMIT=5;
    private Integer GOLD_USER_ACTIVITY_LIMIT=10;


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

        Integer limit = NEW_USER_ACTIVITY_LIMIT;
        String warning  ="50 puan altı olduğun için haftada en fazla " + limit + " aktivite açabilirsin!";

        if (user.getGender() == Gender.MALE && user.getPoint() > 50) {
            limit = OLD_USER_ACTIVITY_LIMIT;
            warning = "Haftada en fazla " + limit + " aktivite açabilirsin!";
        }


        Premium premium = premiumService.isUserPremium(user);
         if(premium!=null){

             if(premium.getDuration()== PremiumDuration.SONE_MONTH || premium.getDuration()==PremiumDuration.STHREE_MONTHS || premium.getDuration()==PremiumDuration.SSIX_MONTHS){
                 limit = SILVER_USER_ACTIVITY_LIMIT;
                 warning = "Silver kullanıcılar haftada en fazla " + limit + " aktivite açabilir!";
             }
             if(premium.getDuration()== PremiumDuration.GONE_MONTH || premium.getDuration()==PremiumDuration.GTHREE_MONTHS || premium.getDuration()==PremiumDuration.GSIX_MONTHS){
                 limit = GOLD_USER_ACTIVITY_LIMIT;
                 warning = "Gold kullanıcılar haftada en fazla " + limit + " aktivite açabilir!";
             }
        }

        if (dayAction != null)
            if (dayAction.getActivityCount() >= limit) {
                throw new UserWarningException(warning);
            }
    }

    public void checkRequestLimit() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayAction = dayActionRepository.findByUser(user);


        Integer limit = NEW_USER_REQUEST_LIMIT;
        String warning = "50 puan altı olduğun için günde en fazla " + limit + " istek gönderebilirsin!";
        if (user.getPoint() > 50) {
            limit = OLD_USER_REQUEST_LIMIT;
            warning="Günde en fazla " + limit + " istek gönderebilirsin!";
        }

        Premium premium = premiumService.isUserPremium(user);
        if(premium!=null){

            if(premium.getDuration()== PremiumDuration.SONE_MONTH || premium.getDuration()==PremiumDuration.STHREE_MONTHS || premium.getDuration()==PremiumDuration.SSIX_MONTHS){
                limit = SILVER_USER_REQUEST_LIMIT;
                warning = "Silver kullanıcılar günde en fazla " + limit + " istek gönderebilir!";
            }
            if(premium.getDuration()== PremiumDuration.GONE_MONTH || premium.getDuration()==PremiumDuration.GTHREE_MONTHS || premium.getDuration()==PremiumDuration.GSIX_MONTHS){
                limit = GOLD_USER_REQUEST_LIMIT;
                warning = "Gold kullanıcılar günde en fazla " + limit + " istek gönderebilir!";
            }
        }

        if (dayAction != null)
            if (dayAction.getRequestCount() >= limit) {
                throw new UserWarningException(warning);
            }
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









