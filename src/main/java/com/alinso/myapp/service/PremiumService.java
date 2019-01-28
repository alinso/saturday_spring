package com.alinso.myapp.service;

import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.repository.PremiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class PremiumService {

    private final double ONE_MONTH_PRICE = 14.90;
    private final double THREE_MONTHS_PRICE = 39.90;
    private final double SIX_MONTHS_PRICE = 69.90;

    @Autowired
    PremiumRepository premiumRepository;

    public void save(Premium premium) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        premium.setUser(loggedUser);
        premiumRepository.save(premium);
    }

    public Boolean isUserPremium(User user) {
        try {
            Premium premium = premiumRepository.findPremiumRecordOfByUser(user).get();

            Calendar  now = Calendar.getInstance();
            now.setTime(new Date());

            Calendar premiumFinish = Calendar.getInstance();
            premiumFinish.setTime(premium.getCreatedAt());

            if (premium.getDuration()== PremiumDuration.ONE_MONTH){
                premiumFinish.add(Calendar.DATE, 30);
                if(now.getTime().compareTo(premiumFinish.getTime())<0)
                    return true;
            }
            if (premium.getDuration()== PremiumDuration.THREE_MONTHS){
                premiumFinish.add(Calendar.DATE, 90);
                if(now.getTime().compareTo(premiumFinish.getTime())<0)
                    return true;
            }
            if (premium.getDuration()== PremiumDuration.SIX_MONTHS){
                premiumFinish.add(Calendar.DATE, 180);
                if(now.getTime().compareTo(premiumFinish.getTime())<0)
                    return true;
            }
            return  false;

        } catch (NoSuchElementException e) {
            return false;
        }
    }

}
