package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRequesRepository;
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
public class ActivityRequestService {

    @Autowired
    ActivityService activityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    UserService userService;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    BlockService blockService;

    @Autowired
    DayActionService dayActionService;

    public Boolean sendRequest(Long id) {

        Activity activity = activityService.findEntityById(id);
        if (activity.getDeadLine().compareTo(new Date()) < 0)
            throw new UserWarningException("Geçmiş tarihli bir aktivitede düzenleme yapamazsınız");




        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (blockService.isThereABlock(activity.getCreator().getId()))
            throw new UserWarningException("Erişim Yok");


        Boolean isThisUserJoined = isThisUserJoined(activity.getId());
        if (!isThisUserJoined) {


            if(activity.getId()!=1640 && activity.getId()!=1856 && activity.getId()!=2079) {
                //check activity req limit
                List<ActivityRequest> allRequests = activityRequesRepository.findByActivityId(id);
                if ( allRequests.size() > 14)
                    throw new UserWarningException("Bu aktivite  dolmuştur, daha fazla istek atılamaz");


                //check male limit
                Integer maleCount = 0;
                for(ActivityRequest r:allRequests){
                    maleCount++;
                }
                if (loggedUser.getGender() == Gender.MALE && maleCount>6)
                    throw new UserWarningException("Bu aktivite  dolmuştur, daha fazla istek atılamaz");

            }

            //check if user reached the limit
            dayActionService.checkRequestLimit();


            ActivityRequest newActivityRequest = new ActivityRequest();
            newActivityRequest.setApplicant(loggedUser);
            newActivityRequest.setActivity(activity);
            newActivityRequest.setActivityRequestStatus(ActivityRequestStatus.WAITING);
            activityRequesRepository.save(newActivityRequest);
            userEventService.newRequest(activity.getCreator(), activity.getId());
            dayActionService.addRequest();
        } else {
            ActivityRequest activityRequest = activityRequesRepository.findByActivityAndApplicant(loggedUser, activity);
            //delete points if this activity request was approved
            //    userEventService.removeApprovedRequestPoints(activityRequest);
            activityRequesRepository.delete(activityRequest);
            dayActionService.removeRequest();
        }

        //we have changed the status in above if-else condition
        //so we need to return opposite of initial value
        return !isThisUserJoined;
    }


    public ActivityRequestStatus approveRequest(Long id) {
        ActivityRequest activityRequest = activityRequesRepository.findById(id).get();

        //check user owner
        UserUtil.checkUserOwner(activityRequest.getActivity().getCreator().getId());

        //check Activity time
        Date now = new Date();
        if(activityRequest.getActivity().getDeadLine().compareTo(now)<0){
            throw new UserWarningException("Tarihi geçmiş aktivitede değişiklik yapamazsın");
        }


        if (blockService.isThereABlock(activityRequest.getActivity().getCreator().getId()))
            throw new UserWarningException("Erişim  yok");


        if (activityRequest.getActivityRequestStatus() == ActivityRequestStatus.WAITING) {
            checkMaxApproveCountExceeded(activityRequest.getActivity());
            activityRequest.setActivityRequestStatus(ActivityRequestStatus.APPROVED);
            activityRequesRepository.save(activityRequest);
            userEventService.newApproval(activityRequest.getApplicant(), activityRequest.getActivity());
        } else {
            activityRequest.setActivityRequestStatus(ActivityRequestStatus.WAITING);
            activityRequesRepository.save(activityRequest);
            //   userEventService.cancelApproval(activityRequest.getApplicant(),activityRequest.getActivity());
        }

        return activityRequest.getActivityRequestStatus();
    }


    public Boolean isThisUserJoined(Long meetingId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isThisUserJoined = false;
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(meetingId);
        for (ActivityRequest activityRequest : activityRequests) {
            if (activityRequest.getApplicant().getId() == loggedUser.getId()) {
                isThisUserJoined = true;
            }
        }
        return isThisUserJoined;
    }

    public void checkMaxApproveCountExceeded(Activity activity) {

        if(activity.getId()==1640 || activity.getId()==1856 || activity.getId()==2079){
            return;
        }

        Integer c = activityRequesRepository.countOfAprrovedForThisActivity(activity, ActivityRequestStatus.APPROVED);
        Integer limit = 6;
        User user = activity.getCreator();
        if (user.getPoint() > 40 && user.getPoint() < 100) {
            limit = 10;
        }else if(user.getPoint()>99 && user.getPoint()<400){
            limit=15;
        }else if(user.getPoint()>399 && user.getPoint()<800){
            limit=15;
        }else if(user.getPoint()>799){
            limit=15;
        }

        if (c == limit) {
            throw new UserWarningException("Her aktivite için en fazla "+limit+" kişi onaylayabilirsiniz");
        }
    }

    public List<ProfileDto> findAttendants(Activity activity) {
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(activity.getId());

        List<User> attendantUsers = new ArrayList<>();
        for (ActivityRequest request : activityRequests) {
            if (request.getActivityRequestStatus() == ActivityRequestStatus.APPROVED)
                attendantUsers.add(request.getApplicant());
        }

        List<ProfileDto> profileDtos = new ArrayList<>();
        profileDtos.addAll(userService.toProfileDtoList(attendantUsers));
        profileDtos.add(userService.toProfileDto(activity.getCreator()));

        return profileDtos;
    }

    public void deleteByActivityId(Long id) {
        for (ActivityRequest activityRequest : activityRequesRepository.findByActivityId(id)) {
            activityRequesRepository.delete(activityRequest);
        }
    }


    public boolean haveTheseUsersMeet(Long id1, Long id2) {

        if (id1 == 3212 || id2 == 3212)
            return true;
        if (id1 == 3211)
            return true;


        User user1 = userService.findEntityById(id1);
        User user2 = userService.findEntityById(id2);


        //if they hosted each other
        Integer user1host = activityRequesRepository.haveUser1HostUser2(user1, user2, ActivityRequestStatus.APPROVED);
        Integer user2host = activityRequesRepository.haveUser1HostUser2(user2, user1, ActivityRequestStatus.APPROVED);

        if (user1host > 0 || user2host > 0)
            return true;


        //if they hosted by saame activity
        List<Activity> activityList1 = activityRequesRepository.activitiesAttendedByUser(user1, ActivityRequestStatus.APPROVED);
        List<Activity> activityList2 = activityRequesRepository.activitiesAttendedByUser(user2, ActivityRequestStatus.APPROVED);


        for (Activity a1 : activityList1) {
            for (Activity a2 : activityList2) {
                if (a1.getId() == a2.getId()) {
                    return true;
                }
            }
        }
        return false;
    }


}
