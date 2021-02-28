package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.DayAction;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.DayActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DayActionService {


    private Integer REQUEST_LIMIT=2;
    private Integer EVENT_LIMIT=2;

    private Integer ORGANIZATOR_USER_EVENT_LIMIT=3;
    private  Integer ORGANIZATOR_USER_REQUEST_LIMIT=80;


    @Autowired
    DayActionRepository dayActionRepository;

    @Autowired
    VoteService voteService;

    @Autowired
    FlorinService florinService;


    private DayAction getDayAction() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayActionInDb = dayActionRepository.findByUser(user);

        DayAction newDayAction;
        if (dayActionInDb == null) {
            newDayAction = new DayAction();
            newDayAction.setUser(user);
            newDayAction.setRequestCount(0);
            newDayAction.setEventCount(0);
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

    public void addEvent() {
        DayAction newDayAction = getDayAction();
        newDayAction.setEventCount(newDayAction.getEventCount() + 1);
        dayActionRepository.save(newDayAction);
    }


    public void checkEventLimit() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayAction = dayActionRepository.findByUser(user);
        if(dayAction!=null){
            if(dayAction.getEventCount()>=EVENT_LIMIT)
                florinService.eventExcess(user);
        }
    }

    public void checkRequestLimit() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DayAction dayAction = dayActionRepository.findByUser(user);
        if(dayAction!=null){
            if(dayAction.getRequestCount()>=REQUEST_LIMIT)
                florinService.requestExcess(user);
            else
                florinService.sendRequest(user);
        }
    }


    @Scheduled(cron = "0 0 1 * * MON")
    private void clearEvent() {
        dayActionRepository.clearEvent();
    }

    @Scheduled(cron = "0 0 1 * * MON")
    private void clearRequest() {
        dayActionRepository.clearRequest();
    }


}









