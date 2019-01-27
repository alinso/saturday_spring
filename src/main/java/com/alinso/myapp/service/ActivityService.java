package com.alinso.myapp.service;

import com.alinso.myapp.dto.activity.ActivityDto;
import com.alinso.myapp.dto.activity.ActivityRequestDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ActivityService {

   @Autowired
   HashtagService hashtagService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

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

    @Value("${upload.path}")
    private String fileUploadPath;


    public ActivityDto findById(Long id) {
        Activity activity = activityRepository.findById(id).get();

        ProfileDto profileDto = modelMapper.map(activity.getCreator(), ProfileDto.class);

        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setProfileDto(profileDto);
        activityDto.setDeadLineString(DateUtil.dateToString(activity.getDeadLine(),"dd/MM/yyyy HH:mm"));
        activityDto.setThisUserJoined(activityRequestService.isThisUserJoined(activity.getId()));
        activityDto.setAttendants(activityRequestService.findAttendants(activity));

        activityDto.setHashtagListString(hashtagService.findByActivityStr(activity));

        if(activity.getDeadLine().compareTo(new Date()) < 0)
            activityDto.setExpired(true);
        else
            activityDto.setExpired(false);

        return activityDto;

    }


    public Activity save(ActivityDto activityDto) {

        Activity activity = new Activity(activityDto.getDetail());
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        City city = cityService.findById(activityDto.getCityId());

        activity.setCommentNotificationSent(false);
        activity.setCity(city);
        activity.setCreator(loggedUser);
        activity.setDeadLine(DateUtil.stringToDate(activityDto.getDeadLineString(),"dd/MM/yyyy HH:mm"));
        activity.setPhotoName(fileStorageUtil.saveFileAndReturnName(activityDto.getFile(),fileUploadPath));

        activityRepository.save(activity);
        userEventService.newMeeting(loggedUser, activity);
        hashtagService.saveActivityHashtag(activity,activityDto.getHashtagListString());

        return activity;
    }

    public Activity update(ActivityDto activityDto) {

        Activity activityInDb = activityRepository.findById(activityDto.getId()).get();

        //check user owner
        UserUtil.checkUserOwner(activityInDb.getCreator().getId());

        City city = cityService.findById(activityDto.getCityId());
        activityInDb.setCity(city);


        //save new photo and remove old one
        if (activityDto.getFile() != null) {
            fileStorageUtil.deleteFile(fileUploadPath + activityInDb.getPhotoName());
            activityInDb.setPhotoName(fileStorageUtil.saveFileAndReturnName(activityDto.getFile(),fileUploadPath));
        }

        activityInDb.setDeadLine(DateUtil.stringToDate(activityDto.getDeadLineString(),"dd/MM/yyyy HH:mm"));
        activityInDb.setDetail(activityDto.getDetail());

        hashtagService.saveActivityHashtag(activityInDb,activityDto.getHashtagListString());


        return activityRepository.save(activityInDb);



    }

    public List<ActivityDto> findAllNonExpiredByCityId(Long cityId) {

        List<Activity> activities = activityRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(),cityService.findById(cityId));
        List<ActivityDto> activityDtos = new ArrayList<>();

        for (Activity activity : activities) {

            if(blockService.isThereABlock(activity.getCreator().getId()))
                continue;

            ProfileDto profileDto = modelMapper.map(activity.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(activity.getCreator()));

            ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
            activityDto.setThisUserJoined(activityRequestService.isThisUserJoined(activity.getId()));
            activityDto.setDeadLineString(DateUtil.dateToString(activity.getDeadLine(),"dd/MM/yyyy HH:mm"));
            activityDto.setProfileDto(profileDto);
            activityDto.setHashtagListString(hashtagService.findByActivityStr(activity));
            activityDtos.add(activityDto);
        }
        return activityDtos;
    }


    public void deleteById(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Activity activityInDb = activityRepository.findById(id).get();

        //check user authorized
        UserUtil.checkUserOwner(activityInDb.getCreator().getId());

        //decrease user's point & count
        userEventService.removeMeeting(activityInDb);

        //delete file
        fileStorageUtil.deleteFile(fileUploadPath + activityInDb.getPhotoName());

        //delete requests
       activityRequestService.deleteByActivityId(id);

       //delete hashtags
        hashtagService.deleteByActivity(activityInDb);

        activityRepository.deleteById(id);
    }

    public List<ActivityDto> activitiesOfUser(Long id) {
        User user = userRepository.findById(id).get();


        if(blockService.isThereABlock(id))
            throw  new UserWarningException("Erişim Yok");

        List<Activity> meetingsCreatedByUser = activityRepository.findByCreatorOrderByDeadLineDesc(user);
        List <Activity> meetingsAttendedByUser  = activityRequesRepository.activitiesAttendedByUser(user, ActivityRequestStatus.APPROVED);

        List<Activity> activities = new ArrayList<>();
        activities.addAll(meetingsCreatedByUser);
        activities.addAll(meetingsAttendedByUser);

        List<ActivityDto> activityDtos = new ArrayList<>();
        for (Activity activity : activities) {

            ProfileDto profileDto = modelMapper.map(activity.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(activity.getCreator()));

            ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
            activityDto.setDeadLineString(DateUtil.dateToString(activity.getDeadLine(),"dd/MM/yyyy hh:mm"));
            if(activity.getDeadLine().compareTo(new Date()) < 0)
                activityDto.setExpired(true);
            else
                activityDto.setExpired(false);

            activityDto.setAttendants(activityRequestService.findAttendants(activity));
            activityDto.setThisUserJoined(activityRequestService.isThisUserJoined(activity.getId()));
            activityDto.setProfileDto(profileDto);
            activityDto.setHashtagListString(hashtagService.findByActivityStr(activity));
            activityDtos.add(activityDto);
        }
        return activityDtos;
    }

    public ActivityDto getActivityWithRequests(Long id) {

        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(id);
        Activity activity = new Activity();
        try {
            activity = activityRepository.findById(id).get();
        }catch (NoSuchElementException e){
            throw new RecordNotFound404Exception("Sayfa Bulunamadı");
        }
        UserUtil.checkUserOwner(activity.getCreator().getId());

        List<ActivityRequestDto> activityRequestDtos =  new ArrayList<>();
        for(ActivityRequest activityRequest : activityRequests){
            ActivityRequestDto activityRequestDto = modelMapper.map(activityRequest, ActivityRequestDto.class);

            ProfileDto profileDto  =modelMapper.map(activityRequest.getApplicant(),ProfileDto.class);
            Integer age = UserUtil.calculateAge(activityRequest.getApplicant());
            profileDto.setAge(age);

            activityRequestDto.setProfileDto(profileDto);
            activityRequestDtos.add(activityRequestDto);
        }

        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setDeadLineString(DateUtil.dateToString(activity.getDeadLine(),"dd/MM/yyyy hh:mm"));
        activityDto.setHashtagListString(hashtagService.findByActivityStr(activity));
        activityDto.setRequests(activityRequestDtos);

        return activityDto;
    }
}
























