package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.entity.dto.activity.ActivityRequestDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.exception.RecordNotFound404Exception;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.CategoryRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    UserService userService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HashtagService hashtagService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    ActivityPhotoService activityPhotoService;

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

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowService followService;

    public ActivityDto findById(Long id) {

        Activity activity = null;
        try {
            activity = activityRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Aktivite Bulunamadı");
        }

        if(blockService.isThereABlock(activity.getCreator().getId()))
            throw new UserWarningException("Erişim Yok");
        if(!canSeeListActivity(activity))
            throw new UserWarningException("Erişim Yok");


        ActivityDto activityDto = toDto(activity);


        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(activity.getId());
        List<ActivityRequestDto> activityRequestDtos = new ArrayList<>();

        if (activityRequestService.isThisUserApprovedAllTimes(activity)) { //only attedndats cann see attendants
            for (ActivityRequest activityRequest : activityRequests) {
                if ((activityRequest.getActivityRequestStatus() == ActivityRequestStatus.APPROVED)) { //only approved users should be seen
                    ActivityRequestDto activityRequestDto = new ActivityRequestDto();
                    activityRequestDto.setProfileDto(userService.toProfileDto(activityRequest.getApplicant()));
                    activityRequestDto.setActivityRequestStatus(activityRequest.getActivityRequestStatus());
                    activityRequestDto.setId(activityRequest.getId());
                    activityRequestDto.setResult(activityRequest.getResult());

                    activityRequestDtos.add(activityRequestDto);
                }
            }
            activityDto.setRequests(activityRequestDtos);
        }

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
        activity.setSecret(activityDto.getSecret());

        Iterable<Long> ids = activityDto.getSelectedCategoryIds();
        List<Category> categoryList  = categoryRepository.findAllById(ids);
        Set<Category> categorySet = categoryList.stream().collect(Collectors.toSet());
        activity.setCategories(categorySet);

        activityRepository.save(activity);
        userEventService.newActivity(loggedUser, activity);
        //hashtagService.saveActivityHashtag(activity, activityDto.getHashtagListString());
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

        Iterable<Long> ids = activityDto.getSelectedCategoryIds();
        List<Category> categoryList  = categoryRepository.findAllById(ids);
        Set<Category> categorySet = categoryList.stream().collect(Collectors.toSet());
        activityInDb.setCategories(categorySet);

        //hashtagService.saveActivityHashtag(activityInDb, activityDto.getHashtagListString());
        return activityRepository.save(activityInDb);
    }

    public List<ActivityDto> findAllNonExpiredByCityId(Long cityId, Integer pageNum) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Pageable pageable = PageRequest.of(pageNum, 10);
        List<Activity> activities = activityRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(), cityService.findById(cityId), pageable);
        List<ActivityDto> activityDtos = new ArrayList<>();

        //balon futbolu
        //  User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pageNum == 0) {

            user.setLastLogin(new Date());
            userRepository.save(user);


            //     Activity selected2 = activityRepository.findById(Long.valueOf(9358)).get();
            //   Activity selected = activityRepository.findById(Long.valueOf(9361)).get();
            // Activity selected3 = activityRepository.findById(Long.valueOf(9256)).get();
            //   activityDtos.add(toDto(selected2));
            //   activityDtos.add(toDto(selected));
            // activityDtos.add(toDto(selected3));

        }

        for (Activity activity : activities) {

            if (blockService.isThereABlock(activity.getCreator().getId()))
                continue;

            if (!canSeeListActivity(activity))
                continue;

            ActivityDto activityDto = toDto(activity);
            activityDtos.add(activityDto);
        }
        return activityDtos;
    }


    private boolean canSeeListActivity(Activity activity) {
        if (!activity.getSecret()) {
            return true;
        }
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(activity.getCreator().getId()==loggedUser.getId()){
            return true;
        }

        List<User> followings = followService.findFollowingsOfUser(activity.getCreator());
        for (User following : followings) {
            if (loggedUser.getId() == following.getId()) {
                return true;
            }
        }
        return false;
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

        //delete activity Album
        activityPhotoService.deleteByActivity(activityInDb);

        activityRepository.deleteById(id);
    }


    public void cannotEditInLAstTwoHours(Activity activityInDb) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (u.getId() == 3211)
            return;

        if (
                activityInDb.getDeadLine().compareTo(DateUtil.xHoursLater(2)) < 0
        )
            throw new UserWarningException("Aktivite tarihine 2 saatten  az kaldığında veya tarihi geçtiğinde aktiviteyi silemez/değiştiremezsin");
    }

    public List<ActivityDto> activitiesOfUser(Long id, Integer pageNum, String type) {
        User user = userService.findEntityById(id);


        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");

        Pageable pageable = PageRequest.of(pageNum,5);

        List<Activity> activities = new ArrayList<>();
        if(type.equals("created"))
        activities = activityRepository.findByCreatorOrderByDeadLineDescPaged(user,pageable);

        if(type.equals("joined"))
           activities= activityRequesRepository.activitiesAttendedByUserPaged(user, ActivityRequestStatus.APPROVED,pageable);


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
       // activityDto.setHashtagListString(hashtagService.findByActivityStr(activity));

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

    public List<Integer> activityCountsOfUser(Long id) {

        User user  = userRepository.findById(id).get();
        List<Integer> integers  = new ArrayList<>();

        Integer userJoinedActivityCount= activityRequesRepository.findApprovedRequestCountByApplicant(user,ActivityRequestStatus.APPROVED);
        Integer userCreatedActivityCount  =activityRepository.countActivitiesByCreator(user);

        integers.add(userCreatedActivityCount);
        integers.add(userJoinedActivityCount);

        return integers;
    }

    public List<ActivityDto> findAllNonExpiredByCategoriesByCityId(Long cityId, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 10);
        List<Activity> activities = activityRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(), cityService.findById(cityId), pageable);

        userService.setLastLogin();

        return filterActivities(activities,true);
    }


    public List<ActivityDto> all(Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        List<Activity> activities = activityRepository.findAllOrderByDeadLineAsc(new Date(),pageable);

        return filterActivities(activities,false);
    }


    public List<ActivityDto> filterActivities(List<Activity> activityList, Boolean filterCategory){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ActivityDto> activityDtos = new ArrayList<>();
        for (Activity activity : activityList) {

            if (blockService.isThereABlock(activity.getCreator().getId()))
                continue;

            if (!canSeeListActivity(activity))
                continue;

            if(filterCategory) {
                Boolean inCategory = false;
                for (Category userCategory : user.getCategories()) {
                    for (Category activityCategory : activity.getCategories()) {
                        if (activityCategory.getId() == userCategory.getId()) {
                            inCategory = true;
                            break;
                        }
                    }
                }
                if(!inCategory)
                    continue;
            }



            ActivityDto activityDto = toDto(activity);
            activityDtos.add(activityDto);
        }
        return activityDtos;
    }

    public List<ActivityDto> allActivitiesOfUser(Long id) {
        User user = userService.findEntityById(id);


        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");


        List<Activity> activities = new ArrayList<>();
            activities.addAll(activityRepository.findByCreatorOrderByDeadLineDesc(user));
            activities.addAll(activityRequesRepository.activitiesAttendedByUser(user, ActivityRequestStatus.APPROVED));


        return filterActivities(activities,false);
    }
}
























