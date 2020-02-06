package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
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


    private Integer MALE_USER_REQUEST_LIMIT=1;
    private Integer FEMALE_USER_REQUEST_LIMIT=15;
    private Integer SILVER_USER_REQUEST_LIMIT=50;
    private Integer GOLD_USER_REQUEST_LIMIT=80;


    private Integer MALE_USER_ACTIVITY_LIMIT=1;
    private Integer FEMALE_USER_ACTIVITY_LIMIT=2;
    private Integer SILVER_USER_ACTIVITY_LIMIT=5;
    private Integer GOLD_USER_ACTIVITY_LIMIT=10;

    private Integer ORGANIZATOR_USER_ACTIVITY_LIMIT=3;
    private  Integer ORGANIZATOR_USER_REQUEST_LIMIT=80;


    @Autowired
    DayActionRepository dayActionRepository;

    @Autowired
    VibeService vibeService;


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

        if(user.getTrialUser()==100)
        {
            throw new UserWarningException("Hesabınızın deneme süresi doldu, aktifleştirmek için bir defaya mahsus gold üye olmalısınız");
        }

        Premium premium = premiumService.isUserPremium(user);
        Integer vibe  =vibeService.calculateVibe(user.getId());

        if(vibe<75 && vibe>1 && premium==null && user.getGender()==Gender.MALE){
            throw new UserWarningException("Olumlu izlenim oranı %75 altı olan hesaplar aktivite açamaz");
        }

        Integer limit =MALE_USER_ACTIVITY_LIMIT;
        String warning  ="Haftada en fazla " + limit + " aktivite açabilirsin!";
        if(user.getGender()==Gender.FEMALE){
             limit =FEMALE_USER_ACTIVITY_LIMIT;
             warning  ="Haftada en fazla " + limit + " aktivite açabilirsin!";
        }

;
         if(premium!=null){

             if(premium.getDuration()== PremiumDuration.SONE_MONTH || premium.getDuration()==PremiumDuration.STHREE_MONTHS || premium.getDuration()==PremiumDuration.SSIX_MONTHS){
                 limit = SILVER_USER_ACTIVITY_LIMIT;
                 warning = "Silver kullanıcılar haftada en fazla " + limit + " aktivite açabilir!";
             }
             if(premium.getDuration()== PremiumDuration.GONE_MONTH || premium.getDuration()==PremiumDuration.GTHREE_MONTHS || premium.getDuration()==PremiumDuration.GSIX_MONTHS){
                 limit = GOLD_USER_ACTIVITY_LIMIT;
                 warning = "Gold kullanıcılar haftada en fazla " + limit + " aktivite açabilir!";
             }
             if(premium.getDuration()==PremiumDuration.ORGANIZATOR){
                 limit = ORGANIZATOR_USER_ACTIVITY_LIMIT;
                 warning = "Profesyoneller kullanıcılar haftada en fazla " + limit + " aktivite açabilir!";
             }
        }

        if (dayAction != null)
            if (dayAction.getActivityCount() >= limit) {
                throw new UserWarningException(warning);
            }
    }

    public void checkRequestLimit(Activity activity) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayAction = dayActionRepository.findByUser(user);

        if(user.getTrialUser()==100)
        {
            throw new UserWarningException("Hesabınızın deneme süresi doldu, aktifleştirmek için bir defaya mahsus gold üye olmalısınız");
        }

        Premium premium = premiumService.isUserPremium(user);
        Integer vibe  =vibeService.calculateVibe(user.getId());

        if(vibe<75 && vibe>1 && premium==null && user.getGender()==Gender.MALE){
            throw new UserWarningException("Olumlu izlenim oranı %75 altı olan hesaplar istek gönderemez");
        }



        Integer limit = MALE_USER_REQUEST_LIMIT;
        String warning = "Haftada en fazla " + limit + " istek gönderebilirsin!";
        if (user.getGender() == Gender.FEMALE) {
            limit = FEMALE_USER_REQUEST_LIMIT;
            warning="Haftada en fazla " + limit + " istek gönderebilirsin!";
        }

        //if the activity owner is premium, request limit should be 2
//        Premium ownerPremium = premiumService.isUserPremium(activity.getCreator());
//
//        if(ownerPremium!=null){
//            if(ownerPremium.getDuration()== PremiumDuration.GONE_MONTH || ownerPremium.getDuration()==PremiumDuration.GTHREE_MONTHS || ownerPremium.getDuration()==PremiumDuration.GSIX_MONTHS){
//                limit = 2;
//                warning="Günde en fazla " + limit + " istek gönderebilirsin!";
//            }
//        }

        if(premium!=null){

            if(premium.getDuration()== PremiumDuration.SONE_MONTH || premium.getDuration()==PremiumDuration.STHREE_MONTHS || premium.getDuration()==PremiumDuration.SSIX_MONTHS){
                limit = SILVER_USER_REQUEST_LIMIT;
                warning = "Silver kullanıcılar günde en fazla " + limit + " istek gönderebilir!";
            }
            if(premium.getDuration()== PremiumDuration.GONE_MONTH || premium.getDuration()==PremiumDuration.GTHREE_MONTHS || premium.getDuration()==PremiumDuration.GSIX_MONTHS){
                limit = GOLD_USER_REQUEST_LIMIT;
                warning = "Gold kullanıcılar günde en fazla " + limit + " istek gönderebilir!";
            }
            if(premium.getDuration()==PremiumDuration.ORGANIZATOR){
                limit = ORGANIZATOR_USER_REQUEST_LIMIT;
                warning = "Profesyonel kullanıcılar günde en fazla " + limit + " istek gönderebilir!";
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

    @Scheduled(cron = "0 0 1 * * MON")
    private void clearRequest() {
        dayActionRepository.clearRequest();
    }


}









