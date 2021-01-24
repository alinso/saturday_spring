package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.Interest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.EventRepository;
import com.alinso.myapp.repository.InterestRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InterestService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventService eventService;

    public void setUserInterests(List<Long> selectedInterestIds){

        if(selectedInterestIds.size()>10)
            throw new UserWarningException("En fazla 10 ilgi alanı seçebilirsin");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        //substract old data count
        Set<Interest> oldInterestList = user.getInterests();
        for(Interest interest : oldInterestList){
            interest.setWatcherCount(interest.getWatcherCount()-1);
        }
        interestRepository.saveAll(oldInterestList);


        Iterable<Long> ids = selectedInterestIds;
        List<Interest> interestList = interestRepository.findAllById(ids);
        Set<Interest> set = interestList.stream().collect(Collectors.toSet());
        user.setInterests(set);
        userRepository.save(user);


        //add new watchers
        Set<Interest> newInterestList = user.getInterests();
        for(Interest interest : newInterestList){
            interest.setWatcherCount(interest.getWatcherCount()+1);
        }
        interestRepository.saveAll(newInterestList);


    }

    public List<Interest> allInterests() {
        return interestRepository.findAllOrderByNameAsc();
    }

    public Set<Interest> myInterests(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  user.getInterests();
    }


    public List<EventDto> eventsByInterestId(Long id, Integer pageNum) {

        Interest interest = interestRepository.findById(id).get();
        Pageable pageable  = PageRequest.of(pageNum,10);

        List<Event> eventList = eventRepository.findByInterestsOrderByDeadLine(interest,pageable);
        return eventService.filterEvents(eventList,false);

    }
}
