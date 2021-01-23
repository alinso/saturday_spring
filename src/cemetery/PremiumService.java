//package com.alinso.myapp.service;
//
//import com.alinso.myapp.entity.Premium;
//import com.alinso.myapp.entity.User;
//import com.alinso.myapp.entity.dto.user.ProfileDto;
//import com.alinso.myapp.entity.enums.PremiumDuration;
//import com.alinso.myapp.entity.enums.PremiumType;
//import com.alinso.myapp.repository.PremiumRepository;
//import com.alinso.myapp.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//@Service
//public class PremiumService {
//
////    private final double ONE_MONTH_PRICE = 20;
////    private final double THREE_MONTHS_PRICE = 50;
////    private final double SIX_MONTHS_PRICE = 90;
//
//    @Autowired
//    PremiumRepository premiumRepository;
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    UserRepository userRepository;
//
//
//    public void save(Premium premium, Long userId) {
//
//        User toBePremium  =userService.findEntityById(userId);
//
//        if(toBePremium.getTrialUser()==100 || toBePremium.getTrialUser()==99) {
//            toBePremium.setTrialUser(101);
//            userRepository.save(toBePremium);
//        }
//
//        //check if already premium
//        //if already premium set created at last day of current premium
//        if (isUserPremium(toBePremium)!=null)
//            premium.setStartDate(getPremiumLastDate(toBePremium));
//        else
//            premium.setStartDate(new Date());
//
//        premium.setUser(toBePremium);
//        premiumRepository.save(premium);
//    }
//
//    public void saveGift(Premium premium, User user) {
//
//        //check if already premium
//        //if already premium set created at last day of current premium
//        if (isUserPremium(user)!=null)
//            premium.setStartDate(getPremiumLastDate(user));
//        else
//            premium.setStartDate(new Date());
//
//        premium.setUser(user);
//        premium.setType(PremiumType.GIFTED);
//        premiumRepository.save(premium);
//    }
//
////    public void saveGift(User user, PremiumDuration duration) {
////        Premium premium = new Premium();
////        premium.setType(PremiumType.GIFTED);
////        premium.setDuration(duration);
////        if (isUserPremium(user))
////            premium.setStartDate(getPremiumLastDate(user));
////        else
////            premium.setStartDate(new Date());
////        premium.setWriter(user);
////        premiumRepository.save(premium);
////
////    }
//
//    public Date getPremiumLastDate(User user) {
//        //when is final date of premium membership of this use
//        List<Premium> latestPremiumList = premiumRepository.findLatestPremiumRecordOfByUser(user);
//        if (latestPremiumList.size() < 1)
//            return null;
//
//
//        Premium latestPremium = latestPremiumList.get(0);
//
//        Calendar premiumFinish = Calendar.getInstance();
//        premiumFinish.setTime(latestPremium.getStartDate());
//
//        if (latestPremium.getDuration() == PremiumDuration.SONE_MONTH || latestPremium.getDuration() == PremiumDuration.GONE_MONTH || latestPremium.getDuration()==PremiumDuration.ORGANIZATOR) {
//            premiumFinish.add(Calendar.DATE, 30);
//        }
//        if (latestPremium.getDuration() == PremiumDuration.STHREE_MONTHS || latestPremium.getDuration() == PremiumDuration.GTHREE_MONTHS) {
//            premiumFinish.add(Calendar.DATE, 90);
//        }
//        if (latestPremium.getDuration() == PremiumDuration.SSIX_MONTHS || latestPremium.getDuration() == PremiumDuration.GSIX_MONTHS) {
//            premiumFinish.add(Calendar.DATE, 180);
//        }
//        return premiumFinish.getTime();
//    }
//
//
//    public Premium isUserPremium(User user) {
//
//        Calendar now = Calendar.getInstance();
//        now.setTime(new Date());
//
//        Date premiumFinish = getPremiumLastDate(user);
//        if (premiumFinish == null)
//            return null;
//
//        if (now.getTime().compareTo(premiumFinish) > 0)
//            return null;
//
//        List<Premium> latestPremiumList = premiumRepository.findLatestPremiumRecordOfByUser(user);
//        Premium latestPremium = latestPremiumList.get(0);
//
//
//        return latestPremium;
//    }
//
//    public String userPremiumType(User user){
//        Calendar now = Calendar.getInstance();
//        now.setTime(new Date());
//
//        Date premiumFinish = getPremiumLastDate(user);
//        if (premiumFinish == null)
//            return "";
//
//        if (now.getTime().compareTo(premiumFinish) > 0)
//            return "";
//
//        List<Premium> latestPremiumList = premiumRepository.findLatestPremiumRecordOfByUser(user);
//        Premium latestPremium = latestPremiumList.get(0);
//
//        if(latestPremium.getDuration()==PremiumDuration.GONE_MONTH || latestPremium.getDuration()==PremiumDuration.GTHREE_MONTHS || latestPremium.getDuration()==PremiumDuration.GSIX_MONTHS){
//            return "GOLD";
//        }
//        if(latestPremium.getDuration()==PremiumDuration.SONE_MONTH || latestPremium.getDuration()==PremiumDuration.STHREE_MONTHS || latestPremium.getDuration()==PremiumDuration.SSIX_MONTHS){
//            return "SILVER";
//        }
//        if(latestPremium.getDuration()==PremiumDuration.ORGANIZATOR){
//            return "ORGANIZATOR";
//        }
//        return null;
//    }
//
//    public List<ProfileDto> findProfessionals() {
//        Calendar aMonthAgo = Calendar.getInstance();
//        aMonthAgo.add(Calendar.DATE,-30);
//
//        List<User> premimUserList = new ArrayList<>();
//        List<Premium> premiums = premiumRepository.findByDuration(PremiumDuration.ORGANIZATOR);
//
//        for(Premium p:premiums){
//               if(p.getCreatedAt().compareTo(aMonthAgo.getTime())>0){
//                   premimUserList.add(p.getUser());
//               }
//        }
//        return userService.toProfileDtoList(premimUserList);
//    }
//}
