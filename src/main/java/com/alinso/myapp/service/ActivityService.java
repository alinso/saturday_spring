package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.entity.dto.activity.ActivityRequestDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ActivityService {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HashtagService hashtagService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    UserEventService userEventService;

    @Autowired
    CityService cityService;

    @Autowired
    ActivityRequestService activityRequestService;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    BlockService blockService;

    @Autowired
    DayActionService dayActionService;

    @Autowired
    AdminService adminService;


    public ActivityDto findById(Long id) {
        Activity activity = null;
        try {
            activity = activityRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Aktivite Bulunamadı");
        }

        ActivityDto activityDto = toDto(activity);

        if (!activityRequestService.isThisUserApprovedAllTimes(activity))
            activityDto.setAttendants(null);

        return activityDto;

    }


    public Activity findEntityById(Long id) {
        Activity activity = null;
        try {
            activity = activityRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Aktivite Bulunamadı");
        }
        return activity;
    }


    public Activity save(ActivityDto activityDto) {

        //check if user reached the limit
        dayActionService.checkActivityLimit();

        Activity activity = new Activity(activityDto.getDetail());
        City city = cityService.findById(activityDto.getCityId());

        activity.setCity(city);
        activity.setDeadLine(DateUtil.stringToDate(activityDto.getDeadLineString(), "dd/MM/yyyy HH:mm"));
        activity.setPhotoName(fileStorageUtil.saveFileAndReturnName(activityDto.getFile()));

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        activity.setCreator(loggedUser);
        activity.setCommentNotificationSent(false);

        activityRepository.save(activity);
        userEventService.newActivity(loggedUser, activity);
        hashtagService.saveActivityHashtag(activity, activityDto.getHashtagListString());
        dayActionService.addActivity();
        return activity;
    }

    public Activity update(ActivityDto activityDto) {

        Activity activityInDb = activityRepository.findById(activityDto.getId()).get();
        //check user owner
        UserUtil.checkUserOwner(activityInDb.getCreator().getId());

        cannotEditInLAstTwoHours(activityInDb);

        //check time
        Date now = new Date();
        if (activityInDb.getDeadLine().compareTo(now) < 0) {
            throw new UserWarningException("Tarihi geçmiş aktivitede değişiklik yapamazsın");
        }

        City city = cityService.findById(activityDto.getCityId());
        activityInDb.setCity(city);

        //save new photo and remove old one
        if (activityDto.getFile() != null) {
            fileStorageUtil.deleteFile(activityInDb.getPhotoName());
            activityInDb.setPhotoName(fileStorageUtil.saveFileAndReturnName(activityDto.getFile()));
        }
        activityInDb.setDeadLine(DateUtil.stringToDate(activityDto.getDeadLineString(), "dd/MM/yyyy HH:mm"));
        activityInDb.setDetail(activityDto.getDetail());

        hashtagService.saveActivityHashtag(activityInDb, activityDto.getHashtagListString());
        return activityRepository.save(activityInDb);
    }

    public List<ActivityDto> findAllNonExpiredByCityId(Long cityId, Integer pageNum) {


        Pageable pageable = PageRequest.of(pageNum, 10);
        List<Activity> activities = activityRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(), cityService.findById(cityId), pageable);
        List<ActivityDto> activityDtos = new ArrayList<>();

        //balon futbolu
        if (pageNum == 0) {
            //   Activity selected = activityRepository.findById(Long.valueOf(3775)).get();
            //   activityDtos.add(toDto(selected));
        }

        for (Activity activity : activities) {

            if (blockService.isThereABlock(activity.getCreator().getId()))
                continue;

            ActivityDto activityDto = toDto(activity);
            activityDtos.add(activityDto);
        }
        return activityDtos;
    }


    public void deleteById(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Activity activityInDb = activityRepository.findById(id).get();

        //check user authorized
        UserUtil.checkUserOwner(activityInDb.getCreator().getId());
        if (user.getId() == 3211)
            adminService.deleteActivity(id);


        cannotEditInLAstTwoHours(activityInDb);

        //decrease user's point & count
        //     userEventService.removeMeeting(activityInDb);

        //delete file
        fileStorageUtil.deleteFile(activityInDb.getPhotoName());

        //delete requests
        activityRequestService.deleteByActivityId(id);

        //delete hashtags
        hashtagService.deleteByActivity(activityInDb);

        //delete dayAction
        dayActionService.removeActivity();

        activityRepository.deleteById(id);
    }


    public void cannotEditInLAstTwoHours(Activity activityInDb){
        if (
                activityInDb.getDeadLine().compareTo(DateUtil.xHoursLater(2)) < 0
                        &&
                        activityInDb.getDeadLine().compareTo(new Date()) > 0
        )
            throw new UserWarningException("Son 2 saatte  aktiviteyi silemez/değiştiremezsin");
    }

    public List<ActivityDto> activitiesOfUser(Long id) {
        User user = userService.findEntityById(id);


        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");

        List<Activity> meetingsCreatedByUser = activityRepository.findByCreatorOrderByDeadLineDesc(user);
        List<Activity> meetingsAttendedByUser = activityRequesRepository.activitiesAttendedByUser(user, ActivityRequestStatus.APPROVED);

        List<Activity> activities = new ArrayList<>();
        activities.addAll(meetingsCreatedByUser);
        activities.addAll(meetingsAttendedByUser);

        List<ActivityDto> activityDtos = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityDto activityDto = toDto(activity);
            activityDtos.add(activityDto);
        }
        return activityDtos;
    }

    public ActivityDto getActivityWithRequests(Long id) {

        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(id);
        Activity activity = null;
        try {
            activity = activityRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new RecordNotFound404Exception("Sayfa Bulunamadı");
        }
        UserUtil.checkUserOwner(activity.getCreator().getId());

        List<ActivityRequestDto> activityRequestDtos = new ArrayList<>();
        for (ActivityRequest activityRequest : activityRequests) {
            ActivityRequestDto activityRequestDto = modelMapper.map(activityRequest, ActivityRequestDto.class);
            activityRequestDto.setProfileDto(userService.toProfileDto(activityRequest.getApplicant()));
            activityRequestDtos.add(activityRequestDto);
        }

        ActivityDto activityDto = toDto(activity);
        activityDto.setRequests(activityRequestDtos);

        return activityDto;
    }

    public ActivityDto toDto(Activity activity) {

        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setProfileDto(userService.toProfileDto(activity.getCreator()));
        activityDto.setDeadLineString(DateUtil.dateToString(activity.getDeadLine(), "dd/MM/yyyy HH:mm"));
        activityDto.setThisUserJoined(activityRequestService.isThisUserJoined(activity.getId()));
        activityDto.setAttendants(activityRequestService.findAttendants(activity));
        activityDto.setHashtagListString(hashtagService.findByActivityStr(activity));

        if (activity.getDeadLine().compareTo(new Date()) < 0)
            activityDto.setExpired(true);
        else
            activityDto.setExpired(false);

        return activityDto;
    }

    public List<ActivityDto> toDtoList(List<Activity> activities) {
        List<ActivityDto> activityDtos = new ArrayList<>();
        for (Activity a : activities) {
            activityDtos.add(toDto(a));
        }
        return activityDtos;
    }
}
























