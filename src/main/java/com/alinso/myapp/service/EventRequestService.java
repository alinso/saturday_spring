package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.EventRequestRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EventRequestService {

    @Autowired
    EventService eventService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    UserService userService;

    @Autowired
    EventRequestRepository eventRequestRepository;

    @Autowired
    BlockService blockService;

    @Autowired
    DayActionService dayActionService;


    @Autowired
    PremiumService premiumService;


    @Autowired
    VoteService voteService;

    public void saveResult(Long requestId, Integer result) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        EventRequest eventRequest = eventRequestRepository.findById(requestId).get();
        if (eventRequest.getEvent().getCreator().getId() == loggedUser.getId()) {

            eventRequest.setResult(result);
            eventRequestRepository.save(eventRequest);

            if(result==0){
                voteService.deleteVotesOfNonComingUser(eventRequest.getEvent(), eventRequest.getApplicant());
            }
            if(result==1) {
                voteService.recoverVotesOfApplicant(eventRequest.getApplicant());
            }
        }
    }


    public Integer sendRequest(Long id) {

        Event event = eventService.findEntityById(id);
        if (event.getDeadLine().compareTo(new Date()) < 0)
            throw new UserWarningException("Geçmiş tarihli bir aktivitede düzenleme yapamazsınız");

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (blockService.isThereABlock(event.getCreator().getId()))
            throw new UserWarningException("Erişim Yok");


        Integer isThisUserJoined = isThisUserJoined(event.getId());
        if (isThisUserJoined==0) {


            if (event.getCreator().getId() != 3212 && event.getCreator().getId() != 448) {



                //check event req limit
                List<EventRequest> allRequests = eventRequestRepository.findByEventId(id);

                if (allRequests.size() > 14 &&)
                    throw new UserWarningException("Bu aktivite  dolmuştur, daha fazla istek atılamaz");

                //check male limit
                Integer maleCount = 0;
                for (EventRequest r : allRequests) {
                    if (r.getApplicant().getGender() == Gender.MALE)
                        maleCount++;
                }
                if (loggedUser.getGender() == Gender.MALE && maleCount > 4 && event.getCreator().getGender() == Gender.FEMALE )
                    throw new UserWarningException("Bu aktivite  dolmuştur, daha fazla istek atılamaz");
            }

            //check if user reached the limit
            dayActionService.checkRequestLimit(event);


            EventRequest newEventRequest = new EventRequest();
            newEventRequest.setApplicant(loggedUser);
            newEventRequest.setEvent(event);
            newEventRequest.setEventRequestStatus(EventRequestStatus.WAITING);
            eventRequestRepository.save(newEventRequest);
            userEventService.newRequest(event.getCreator(), event.getId());
            dayActionService.addRequest();
            isThisUserJoined=1;

        } else if(isThisUserJoined==1 || isThisUserJoined==2){
            EventRequest eventRequest = eventRequestRepository.findByEventAndApplicant(loggedUser, event);
            //    userEventService.removeApprovedRequestPoints(activityRequest);


            if (event.getDeadLine().compareTo(DateUtil.xHoursLater(2)) < 0)
                throw new UserWarningException("Son 2 saatte isteği iptal edemezsin");

            eventRequestRepository.delete(eventRequest);
            isThisUserJoined=0;

            //delete points if this activity request was approved
            voteService.deleteVotesOfNonComingUser(eventRequest.getEvent(),loggedUser);
        }

       return isThisUserJoined;
    }


    //this is used for messaging and message_activity
    public Boolean isThisUserApprovedTwoDaysLimit(Event event) {

        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (u.getId() == 3211)
            return true;

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DATE, -2);

        if (event.getDeadLine().compareTo(now.getTime()) < 0)
            return false;

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (event.getCreator().getId() == loggedUser.getId())
            return true;

        Boolean isThisUserApproved = false;
        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(event.getId());
        for (EventRequest eventRequest : eventRequests) {
            if (eventRequest.getApplicant().getId() == loggedUser.getId() && eventRequest.getEventRequestStatus().equals(EventRequestStatus.APPROVED)) {
                isThisUserApproved = true;
            }
        }
        return isThisUserApproved;
    }

    public Boolean isThisUserApprovedAllTimes(Event event) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (event.getCreator().getId() == loggedUser.getId())
            return true;

        if(premiumService.userPremiumType(loggedUser)=="GOLD" || premiumService.userPremiumType(loggedUser)=="ORGANIZATOR")
            return true;

        Boolean isThisUserApproved = false;
        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(event.getId());
        for (EventRequest eventRequest : eventRequests) {
            if (eventRequest.getApplicant().getId() == loggedUser.getId() && eventRequest.getEventRequestStatus().equals(EventRequestStatus.APPROVED)) {
                isThisUserApproved = true;
            }
        }
        return isThisUserApproved;
    }

    public EventRequestStatus approveRequest(Long id) {
        EventRequest eventRequest = eventRequestRepository.findById(id).get();

        //check user owner
        UserUtil.checkUserOwner(eventRequest.getEvent().getCreator().getId());

        //check Activity time
        Date now = new Date();
        if (eventRequest.getEvent().getDeadLine().compareTo(now) < 0) {
            throw new UserWarningException("Tarihi geçmiş aktivitede değişiklik yapamazsın");
        }


        if (blockService.isThereABlock(eventRequest.getEvent().getCreator().getId()))
            throw new UserWarningException("Erişim  yok");


        if (eventRequest.getEventRequestStatus() == EventRequestStatus.WAITING) {
            checkMaxApproveCountExceeded(eventRequest.getEvent());
            eventRequest.setEventRequestStatus(EventRequestStatus.APPROVED);
            eventRequest.setResult(1);
            eventRequestRepository.save(eventRequest);
            userEventService.newApproval(eventRequest.getApplicant(), eventRequest.getEvent());
        } else {
            eventRequest.setResult(null);
            eventRequest.setEventRequestStatus(EventRequestStatus.WAITING);
            eventRequestRepository.save(eventRequest);
            voteService.deleteVotesOfNonComingUser(eventRequest.getEvent(), eventRequest.getApplicant());
        }

        return eventRequest.getEventRequestStatus();
    }


    public Integer isThisUserJoined(Long meetingId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer isThisUserJoined = 0;
        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(meetingId);
        for (EventRequest eventRequest : eventRequests) {
            if (eventRequest.getApplicant().getId() == loggedUser.getId()) {
                isThisUserJoined = 1;
                if(eventRequest.getEventRequestStatus()== EventRequestStatus.APPROVED) {
                    isThisUserJoined = 2;
                }
                break;
            }

        }
        return isThisUserJoined;
    }

    public void checkMaxApproveCountExceeded(Event event) {

        if (event.getCreator().getId() == 3212 || event.getCreator().getId() == 448) {
            return;
        }

        Integer c = eventRequestRepository.countOfAprrovedForThisEvent(event, EventRequestStatus.APPROVED);
        User user = event.getCreator();


        Integer limit=8;

        String premiumType = premiumService.userPremiumType(user);
        if (premiumType.equals("GOLD")) {
            limit = 12;
        }
        if (premiumType.equals("SILVER")) {
            limit = 12;
        }
        if (premiumType.equals("ORGANIZATOR")) {
            limit = 9999;
        }


        if (c == limit) {
            throw new UserWarningException("Her aktivite için en fazla " + limit + " kişi onaylayabilirsin");
        }
    }

    public List<ProfileDto> findAttendants(Event event) {
        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(event.getId());

        List<User> attendantUsers = new ArrayList<>();
        for (EventRequest request : eventRequests) {
            if (request.getEventRequestStatus() == EventRequestStatus.APPROVED)
                attendantUsers.add(request.getApplicant());
        }

        List<ProfileDto> profileDtos = new ArrayList<>();
        profileDtos.addAll(userService.toProfileDtoList(attendantUsers));
        profileDtos.add(userService.toProfileDto(event.getCreator()));

        return profileDtos;
    }

    public List<User> findAttendantEntities(Event event) {
        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(event.getId());

        List<User> attendantUsers = new ArrayList<>();
        for (EventRequest request : eventRequests) {
            if (request.getEventRequestStatus() == EventRequestStatus.APPROVED)
                attendantUsers.add(request.getApplicant());
        }

        return attendantUsers;
    }

    public void deleteByEventId(Long id) {
        for (EventRequest eventRequest : eventRequestRepository.findByEventId(id)) {
            eventRequestRepository.delete(eventRequest);
        }
    }


    public boolean haveTheseUsersMeet(Long id1, Long id2) {

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DATE, -2);


        if (id1 == 3212 || id2 == 3212 || id1==448 || id2==448)
            return true;


        if (id2 == 3211 )
            return true;


        User user1 = userService.findEntityById(id1);
        User user2 = userService.findEntityById(id2);


        //if they hosted each other
        Integer user1host = eventRequestRepository.haveUser1HostUser2(user1, user2, EventRequestStatus.APPROVED, now.getTime());
        Integer user2host = eventRequestRepository.haveUser1HostUser2(user2, user1, EventRequestStatus.APPROVED, now.getTime());

        if (user1host > 0 || user2host > 0)
            return true;


        //if they hosted by same activity
        List<Event> eventList1 = eventRequestRepository.eventsAttendedByUser(user1, EventRequestStatus.APPROVED);
        List<Event> eventList2 = eventRequestRepository.eventsAttendedByUser(user2, EventRequestStatus.APPROVED);


        for (Event a1 : eventList1) {
            for (Event a2 : eventList2) {
                if (a1.getId() == a2.getId() && a1.getDeadLine().compareTo(now.getTime()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean haveTheseUsersMeetAllTimes(Long id1, Long id2) {


        User user1 = userService.findEntityById(id1);
        User user2 = userService.findEntityById(id2);


        //if they hosted each other
        Integer user1host = eventRequestRepository.haveUser1HostUser2AllTimes(user1, user2, EventRequestStatus.APPROVED);
        Integer user2host = eventRequestRepository.haveUser1HostUser2AllTimes(user2, user1, EventRequestStatus.APPROVED);

        if (user1host > 0 || user2host > 0)
            return true;


        //if they hosted by same activity
        List<Event> eventList1 = eventRequestRepository.eventsAttendedByUser(user1, EventRequestStatus.APPROVED);
        List<Event> eventList2 = eventRequestRepository.eventsAttendedByUser(user2, EventRequestStatus.APPROVED);


        for (Event a1 : eventList1) {

            if(a1.getCreator().getId()==3212 || a1.getCreator().getId()==448)
                continue;

            for (Event a2 : eventList2) {
                if(a1.getCreator().getId()==3212 || a1.getCreator().getId()==448)
                    continue;
                if (a1.getId() == a2.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
