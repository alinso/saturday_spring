package com.alinso.myapp.service;


import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Invitation;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.InvitationRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {

    @Autowired
    InvitationRepository invitationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    PremiumService premiumService;

    @Autowired
    VibeService vibeService;

    @Autowired
    NotificationService notificationService;

    public void send(Long readerId,Long activityId){
        User reader= userRepository.findById(readerId).get();
        Activity activity  = activityRepository.findById(activityId).get();
        User creator  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Integer vibeOfCreator = vibeService.calculateVibe(creator.getId());
        if(vibeOfCreator<85 && !premiumService.userPremiumType(creator).equals("ORGANIZATOR")){
            throw  new UserWarningException("Olumlu izlenim oranı %85 üzeri kullanıcılar davet gönderebilir");
        }

        Invitation invitationInDb = invitationRepository.findByActivityAndReader(activity,reader);
        if(invitationInDb!=null){
            throw  new UserWarningException("Bu kullanıcıyı zaten davet etmiştin");
        }

        if(creator.getId()!=1 ) {
            List<Invitation> invitationList = invitationRepository.findByActivity(activity);
            if (invitationList.size() > 3 && !premiumService.userPremiumType(creator).equals("GOLD") &&!premiumService.userPremiumType(creator).equals("ORGANIZATOR")) {
                throw new UserWarningException("Gold kullanıcılar 25, standart kullanıcılar 3 davet gönderebilir");
            }

            if (invitationList.size() > 25 && premiumService.userPremiumType(creator).equals("GOLD")) {
                throw new UserWarningException("Gold kullanıcılar 25 davet gönderebilir");
            }
            if (invitationList.size() > 100 && premiumService.userPremiumType(creator).equals("ORGANIZATOR")) {
                throw new UserWarningException("Profesyonel kullanıcılar 100 davet gönderebilir");
            }
        }


        if(activity.getCreator().getId()==creator.getId()){
            Invitation invitation = new Invitation();
            invitation.setReader(reader);
            invitation.setActivity(activity);

            invitationRepository.save(invitation);
            notificationService.newInvitation(invitation);
        }
    }

}
















