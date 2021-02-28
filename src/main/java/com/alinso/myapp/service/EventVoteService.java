package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventVote;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.EventRepository;
import com.alinso.myapp.repository.EventVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class EventVoteService {

    @Autowired
    EventVoteRepository voteRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    FlorinService florinService;

    public Integer saveVote(Long eventId, String voteStr) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findById(eventId).get();
        int myVote = Integer.parseInt(voteStr);
        EventVote eventVote = voteRepository.findByVoterAndEvent(loggedUser, event);
        if (eventVote == null)
            eventVote = new EventVote();

        eventVote.setVoter(loggedUser);
        eventVote.setVote(myVote);
        eventVote.setEvent(event);
        voteRepository.save(eventVote);
        int totalVote = eventTotal(eventId);
        event.setVote(totalVote);
        eventRepository.save(event);

        if(myVote==1)
            florinService.eventUpvoted(event.getCreator());
        if(myVote==-1)
            florinService.eventDownvote(event.getCreator());

        return totalVote;
    }

    public int eventTotal(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        int voteTotal = voteRepository.findTotalByEvent(event);
        return voteTotal;
    }

    public int myVote(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EventVote vote  = voteRepository.findByVoterAndEvent(loggedUser,event);
        return vote.getVote();
    }


}























