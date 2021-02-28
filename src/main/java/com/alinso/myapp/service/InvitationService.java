package com.alinso.myapp.service;


import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.Invitation;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.EventRepository;
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
    EventRepository eventRepository;

    @Autowired
    VoteService voteService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FlorinService florinService;

    public void send(Long readerId,Long eventId){
        User reader= userRepository.findById(readerId).get();
        Event event = eventRepository.findById(eventId).get();
        User creator  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Integer votePercentOfOrganiser = voteService.calculateVote(creator.getId());

        Invitation invitationInDb = invitationRepository.findByEventAndReader(event,reader);
        if(invitationInDb!=null){
            throw  new UserWarningException("Bu kullanıcıyı zaten davet etmiştin");
        }

        if(votePercentOfOrganiser<80)
            throw new UserWarningException("You cannot send invitations, please read voting rules.");

        if(creator.getId()!=1 ) {
            List<Invitation> invitationList = invitationRepository.findByEvent(event);
            if (invitationList.size() >3) {
                florinService.invite(creator);
            }
        }


        if(event.getCreator().getId()==creator.getId()){
            Invitation invitation = new Invitation();
            invitation.setReader(reader);
            invitation.setEvent(event);

            invitationRepository.save(invitation);
            notificationService.newInvitation(invitation);
        }
    }

}
















