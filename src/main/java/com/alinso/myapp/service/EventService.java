package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.entity.dto.event.EventRequestDto;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.entity.enums.FollowStatus;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.*;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    UserService userService;

    @Autowired
    EventViewRepository eventViewRepository;

    @Autowired
    EventVoteRepository eventVoteRepository;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    EventPhotoService eventPhotoService;

    @Autowired
    UserEventService userEventService;

    @Autowired
    CityService cityService;

    @Autowired
    EventRequestService eventRequestService;

    @Autowired
    EventRequestRepository eventRequestRepository;

    @Autowired
    BlockService blockService;

    @Autowired
    DayActionService dayActionService;

    @Autowired
    AdminService adminService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowService followService;

    @Autowired
    NotificationService notificationService;



    @Scheduled(cron = "0 0 0/2 * * ?")
    public void sendReminderNotification() {
        Calendar finish = Calendar.getInstance();
        finish.setTime(new Date());
        finish.add(Calendar.HOUR, +5);

        Calendar start = Calendar.getInstance();
        start.setTime(new Date());
        start.add(Calendar.HOUR, +3);


        List<Event> eventList = eventRepository.eventsOfDay(start.getTime(), finish.getTime());
        Map<User, Event> attendantsOfDay = new HashMap<>();

        for (Event a : eventList) {
            for (User attendant : eventRequestService.findAttendantEntities(a)) {
                attendantsOfDay.put(attendant, a);
            }
        }

        notificationService.sendReminderOfDay(attendantsOfDay);
    }


    public EventDto findById(Long id) {

        Event event = null;
        try {
            event = eventRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Aktivite Bulunamadı");
        }

        if (blockService.isThereABlock(event.getCreator().getId()))
            throw new UserWarningException("Erişim Yok");
        if (!canSeeEvent(event))
            throw new UserWarningException("Erişim Yok");


        EventDto eventDto = toDto(event);


        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(event.getId());
        List<EventRequestDto> eventRequestDtos = new ArrayList<>();

        if (eventRequestService.isThisUserApprovedAllTimes(event)) { //only attedndats cann see attendants
            for (EventRequest eventRequest : eventRequests) {
                if ((eventRequest.getEventRequestStatus() == EventRequestStatus.APPROVED)) { //only approved users should be seen
                    EventRequestDto eventRequestDto = new EventRequestDto();
                    eventRequestDto.setProfileDto(userService.toProfileDto(eventRequest.getApplicant()));
                    eventRequestDto.setEventRequestStatus(eventRequest.getEventRequestStatus());
                    eventRequestDto.setId(eventRequest.getId());
                    eventRequestDto.setResult(eventRequest.getResult());

                    eventRequestDtos.add(eventRequestDto);
                }
            }
            eventDto.setRequests(eventRequestDtos);
        }

        eventDto.setAttendants(null);

        return eventDto;

    }


    public Event findEntityById(Long id) {
        Event event = null;
        try {
            event = eventRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Aktivite Bulunamadı");
        }
        return event;
    }


    public Event save(EventDto eventDto) {

        //check if user reached the limit
        dayActionService.checkEventLimit();

        Event event = new Event(eventDto.getDetail());
        City city = cityService.findById(eventDto.getCityId());

        event.setCity(city);
        event.setDeadLine(DateUtil.stringToDate(eventDto.getDeadLineString(), "dd/MM/yyyy HH:mm"));
        event.setPhotoName(fileStorageUtil.saveFileAndReturnName(eventDto.getFile()));
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        event.setCreator(loggedUser);
        event.setCommentNotificationSent(false);
        event.setSecret(eventDto.getSecret());

        Iterable<Long> ids = eventDto.getSelectedInterestIds();
        List<Interest> interestList = interestRepository.findAllById(ids);
        Set<Interest> interestSet = interestList.stream().collect(Collectors.toSet());
        event.setInterests(interestSet);

        eventRepository.save(event);
        userEventService.newEvent(loggedUser, event);
        //hashtagService.saveActivityHashtag(activity, activityDto.getHashtagListString());
        dayActionService.addEvent();
        return event;
    }

    public Event update(EventDto eventDto) {

        Event eventInDb = eventRepository.findById(eventDto.getId()).get();
        //check user owner
        UserUtil.checkUserOwner(eventInDb.getCreator().getId());

        cannotEditInLAstTwoHours(eventInDb);

        //check time
        Date now = new Date();
        if (eventInDb.getDeadLine().compareTo(now) < 0) {
            throw new UserWarningException("Tarihi geçmiş aktivitede değişiklik yapamazsın");
        }

        City city = cityService.findById(eventDto.getCityId());
        eventInDb.setCity(city);

        //save new photo and remove old one
        if (eventDto.getFile() != null) {
            fileStorageUtil.deleteFile(eventInDb.getPhotoName());
            eventInDb.setPhotoName(fileStorageUtil.saveFileAndReturnName(eventDto.getFile()));
        }
        eventInDb.setDeadLine(DateUtil.stringToDate(eventDto.getDeadLineString(), "dd/MM/yyyy HH:mm"));
        eventInDb.setDetail(eventDto.getDetail());

        Iterable<Long> ids = eventDto.getSelectedInterestIds();
        List<Interest> interestList = interestRepository.findAllById(ids);
        Set<Interest> interestSet = interestList.stream().collect(Collectors.toSet());
        eventInDb.setInterests(interestSet);

        //hashtagService.saveActivityHashtag(activityInDb, activityDto.getHashtagListString());
        return eventRepository.save(eventInDb);
    }

//    public List<EventDto> findAllNonExpiredByCityId(Long cityId, Integer pageNum) {
//
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//
//        Pageable pageable = PageRequest.of(pageNum, 10);
//        List<Event> activities = eventRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(), cityService.findById(cityId), pageable);
//        List<EventDto> eventDtos = new ArrayList<>();
//
//        //balon futbolu
//        //  User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (pageNum == 0) {
//
//            user.setLastLogin(new Date());
//            userRepository.save(user);
//
//
//            //     Activity selected2 = activityRepository.findById(Long.valueOf(9358)).get();
//            //   Activity selected = activityRepository.findById(Long.valueOf(9361)).get();
//            // Activity selected3 = activityRepository.findById(Long.valueOf(9256)).get();
//            //   activityDtos.add(toDto(selected2));
//            //   activityDtos.add(toDto(selected));
//            // activityDtos.add(toDto(selected3));
//
//        }
//
//        for (Event event : activities) {
//
//            if (blockService.isThereABlock(event.getCreator().getId()))
//                continue;
//
//            if (!canSeeEvent(event))
//                continue;
//
//            EventDto eventDto = toDto(event);
//            eventDtos.add(eventDto);
//        }
//        return eventDtos;
//    }


    private boolean canSeeEvent(Event event) {
        if (!event.getSecret()) {
            return true;
        }
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (event.getCreator().getId() == loggedUser.getId()) {
            return true;
        }

        List<Follow> followers = followService.findFollowersByUser(event.getCreator());
        for (Follow f : followers) {
            if (loggedUser.getId() == f.getFollower().getId() && f.getStatus() == FollowStatus.APPROVED) {
                return true;
            }
        }
        return false;
    }


    public void deleteById(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event eventInDb = eventRepository.findById(id).get();

        //check user authorized
        UserUtil.checkUserOwner(eventInDb.getCreator().getId());
        if (user.getId() == 3211)
            adminService.deleteEvent(id);


        cannotEditInLAstTwoHours(eventInDb);

        //decrease user's point & count
        //     userEventService.removeMeeting(activityInDb);

        //delete file
        fileStorageUtil.deleteFile(eventInDb.getPhotoName());

        //delete requests
        eventRequestService.deleteByEventId(id);

        //delete dayAction
        dayActionService.removeEvent();

        //delete activity Album
        eventPhotoService.deleteByEvent(eventInDb);

        eventRepository.deleteById(id);
    }


    public void cannotEditInLAstTwoHours(Event eventInDb) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (u.getId() == 3211)
            return;

        if (
                eventInDb.getDeadLine().compareTo(DateUtil.xHoursLater(2)) < 0
        )
            throw new UserWarningException("Aktivite tarihine 2 saatten  az kaldığında veya tarihi geçtiğinde aktiviteyi silemez/değiştiremezsin");
    }

    public List<EventDto> eventsOfUser(Long id, Integer pageNum, String type) {
        User user = userService.findEntityById(id);


        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");

        Pageable pageable = PageRequest.of(pageNum, 5);

        List<Event> events = new ArrayList<>();
        if (type.equals("created"))
            events = eventRepository.findByCreatorOrderByDeadLineDescPaged(user, pageable);

        if (type.equals("joined"))
            events = eventRequestRepository.activitiesAttendedByUserPaged(user, EventRequestStatus.APPROVED, pageable);


        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event : events) {
            EventDto eventDto = toDto(event);
            eventDtos.add(eventDto);
        }
        return eventDtos;
    }

    public EventDto getEventWithRequests(Long id) {

        List<EventRequest> eventRequests = eventRequestRepository.findByEventId(id);
        Event event = null;
        try {
            event = eventRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new RecordNotFound404Exception("Sayfa Bulunamadı");
        }
        UserUtil.checkUserOwner(event.getCreator().getId());

        List<EventRequestDto> eventRequestDtos = new ArrayList<>();
        for (EventRequest eventRequest : eventRequests) {
            EventRequestDto eventRequestDto = modelMapper.map(eventRequest, EventRequestDto.class);
            eventRequestDto.setProfileDto(userService.toProfileDto(eventRequest.getApplicant()));
            eventRequestDtos.add(eventRequestDto);
        }

        EventDto eventDto = toDto(event);
        eventDto.setRequests(eventRequestDtos);

        return eventDto;
    }

    public EventDto toDto(Event event) {

        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setProfileDto(userService.toProfileDto(event.getCreator()));
        eventDto.setDeadLineString(DateUtil.dateToString(event.getDeadLine(), "dd/MM/yyyy HH:mm"));
        eventDto.setThisUserJoined(eventRequestService.isThisUserJoined(event.getId()));
        eventDto.setAttendants(eventRequestService.findAttendants(event));
        eventDto.setVote(eventVoteRepository.findTotalByEvent(event));
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        EventVote ev = eventVoteRepository.findByVoterAndEvent(loggedUser, event);
        if(ev!=null)
        eventDto.setMyVote(ev.getVote());

        if (event.getDeadLine().compareTo(new Date()) < 0)
            eventDto.setExpired(true);
        else
            eventDto.setExpired(false);

        EventView view = eventViewRepository.findByEventAndViewer(event,loggedUser);
        if(view==null){
            view = new EventView();
            view.setEvent(event);
            view.setViewer(loggedUser);
            eventViewRepository.save(view);
        }
        return eventDto;
    }

    public List<EventDto> toDtoList(List<Event> events) {
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event a : events) {
            eventDtos.add(toDto(a));
        }
        return eventDtos;
    }

    public List<EventDto> findAllNonExpiredByInterestsByCityId(Long cityId, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 10);
        List<Event> events = eventRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(), cityService.findById(cityId), pageable);

        userService.setLastLogin();

        return filterEvents(events, true);
    }


//    public List<EventDto> all(Integer pageNum) {
//        Pageable pageable = PageRequest.of(pageNum, 10);
//        List<Event> events = eventRepository.findAllOrderByDeadLineAsc(new Date(),pageable);
//
//        return filterEvents(events,false);
//    }


    public List<EventDto> filterEvents(List<Event> eventList, Boolean filterInterest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event : eventList) {

            if (blockService.isThereABlock(event.getCreator().getId()))
                continue;

            if (!canSeeEvent(event))
                continue;

            if (filterInterest) {
                Boolean inInterest = false;
                for (Interest userInterest : user.getInterests()) {
                    for (Interest eventInterest : event.getInterests()) {
                        if (eventInterest.getId() == userInterest.getId()) {
                            inInterest = true;
                            break;
                        }
                    }
                    if (inInterest) //break above cannot break the outer loop so we need this
                        break;
                }
                if (!inInterest)
                    continue;
            }


            EventDto eventDto = toDto(event);
            eventDtos.add(eventDto);
        }
        return eventDtos;
    }

    public List<EventDto> allEventssOfUser(Long id, Integer pageNum) {
        User user = userService.findEntityById(id);

        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");

        Pageable pageable = PageRequest.of(pageNum, 5);

        List<Event> events = new ArrayList<>();
        events.addAll(eventRepository.findByCreatorOrderByDeadLineDescPaged(user, pageable));
        events.addAll(eventRequestRepository.activitiesAttendedByUserPaged(user, EventRequestStatus.APPROVED, pageable));

        return filterEvents(events, false);
    }

    public List<EventDto> findAllNonExpiredByInterestsByCityIdOrderByVote(Long cityId, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        List<Event> events = eventRepository.findAllNonExpiredByCityIdOrderByVote(new Date(), cityService.findById(cityId), pageable);
        userService.setLastLogin();

        return filterEvents(events, true);
    }
}
























