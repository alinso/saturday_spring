package com.alinso.myapp.service;


import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Invitation;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.InvitationRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {

    @Autowired
    InvitationRepository invitationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    NotificationService notificationService;

    public void send(Long readerId,Long activityId){
        User reader= userRepository.findById(readerId).get();
        Activity activity  = activityRepository.findById(activityId).get();
        User creator  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if(activity.getCreator().getId()==creator.getId()){
            Invitation invitation = new Invitation();
            invitation.setReader(reader);
            invitation.setActivity(activity);

            invitationRepository.save(invitation);
            notificationService.newInvitation(invitation);
        }
    }

}
