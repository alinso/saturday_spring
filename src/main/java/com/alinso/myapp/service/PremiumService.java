package com.alinso.myapp.service;

import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.entity.enums.PremiumType;
import com.alinso.myapp.repository.PremiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PremiumService {

    private final double ONE_MONTH_PRICE = 14.90;
    private final double THREE_MONTHS_PRICE = 39.90;
    private final double SIX_MONTHS_PRICE = 69.90;

    @Autowired
    PremiumRepository premiumRepository;

    public void save(Premium premium) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //check if already premium
        //if already premium set created at last day of current premium
        if (isUserPremium(loggedUser))
            premium.setStartDate(getPremiumLastDate(loggedUser));
        else
            premium.setStartDate(new Date());

        premium.setUser(loggedUser);
        premiumRepository.save(premium);
    }

    public void saveGift(User user, PremiumDuration duration) {
        Premium premium = new Premium();
        premium.setType(PremiumType.GIFTED);
        premium.setDuration(duration);
        if (isUserPremium(user))
            premium.setStartDate(getPremiumLastDate(user));
        else
            premium.setStartDate(new Date());
        premium.setUser(user);
        premiumRepository.save(premium);

    }

    public Date getPremiumLastDate(User user) {
        //when is final date of premium membership of this use
        List<Premium> latestPremiumList = premiumRepository.findLatestPremiumRecordOfByUser(user);
        if (latestPremiumList.size() < 1)
            return null;


        Premium latestPremium = latestPremiumList.get(0);

        Calendar premiumFinish = Calendar.getInstance();
        premiumFinish.setTime(latestPremium.getStartDate());

        if (latestPremium.getDuration() == PremiumDuration.ONE_MONTH) {
            premiumFinish.add(Calendar.DATE, 30);
        }
        if (latestPremium.getDuration() == PremiumDuration.THREE_MONTHS) {
            premiumFinish.add(Calendar.DATE, 90);
        }
        if (latestPremium.getDuration() == PremiumDuration.SIX_MONTHS) {
            premiumFinish.add(Calendar.DATE, 180);
        }
        return premiumFinish.getTime();
    }


    public Boolean isUserPremium(User user) {

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        Date premiumFinish = getPremiumLastDate(user);
        if (premiumFinish == null)
            return false;

           /* Calendar premiumFinish = Calendar.getInstance();
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
            }*/

        if (now.getTime().compareTo(premiumFinish) < 0)
            return true;

        return false;

    }

}
