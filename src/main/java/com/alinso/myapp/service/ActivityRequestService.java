package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.Vibe;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRequesRepository;
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


    @Autowired
    PremiumService premiumService;


    @Autowired
    VibeService vibeService;

    public void saveResult(Long requestId, Integer result) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ActivityRequest activityRequest = activityRequesRepository.findById(requestId).get();
        if (activityRequest.getActivity().getCreator().getId() == loggedUser.getId()) {

            activityRequest.setResult(result);
            activityRequesRepository.save(activityRequest);

            if(result==0){
                vibeService.deleteVotesOfNonComingUser(activityRequest.getActivity(),activityRequest.getApplicant());
            }
            if(result==1) {
                vibeService.recoverVibesOfApplicant(activityRequest.getApplicant());
            }
        }
    }


    public Integer sendRequest(Long id) {

        Activity activity = activityService.findEntityById(id);
        if (activity.getDeadLine().compareTo(new Date()) < 0)
            throw new UserWarningException("Geçmiş tarihli bir aktivitede düzenleme yapamazsınız");

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (blockService.isThereABlock(activity.getCreator().getId()))
            throw new UserWarningException("Erişim Yok");


        Integer isThisUserJoined = isThisUserJoined(activity.getId());
        if (isThisUserJoined==0) {


            if (activity.getCreator().getId() != 3212 && activity.getCreator().getId() != 448) {


                String activityCreatorpremiumType = premiumService.userPremiumType(activity.getCreator());
                String requestSenderpremiumType = premiumService.userPremiumType(loggedUser);


                //check activity req limit
                List<ActivityRequest> allRequests = activityRequesRepository.findByActivityId(id);

                if (allRequests.size() > 14 && !activityCreatorpremiumType.equals("GOLD") && !requestSenderpremiumType.equals("GOLD") && !activityCreatorpremiumType.equals("ORGANIZATOR"))
                    throw new UserWarningException("Bu aktivite  dolmuştur, daha fazla istek atılamaz");

                //check male limit
                Integer maleCount = 0;
                for (ActivityRequest r : allRequests) {
                    if (r.getApplicant().getGender() == Gender.MALE)
                        maleCount++;
                }
                if (loggedUser.getGender() == Gender.MALE && maleCount > 4 && activity.getCreator().getGender() == Gender.FEMALE && !requestSenderpremiumType.equals("GOLD")  &&
                        !activityCreatorpremiumType.equals("ORGANIZATOR") )
                    throw new UserWarningException("Bu aktivite  dolmuştur, daha fazla istek atılamaz");
            }

            //check if user reached the limit
            dayActionService.checkRequestLimit(activity);


            ActivityRequest newActivityRequest = new ActivityRequest();
            newActivityRequest.setApplicant(loggedUser);
            newActivityRequest.setActivity(activity);
            newActivityRequest.setActivityRequestStatus(ActivityRequestStatus.WAITING);
            activityRequesRepository.save(newActivityRequest);
            userEventService.newRequest(activity.getCreator(), activity.getId());
            dayActionService.addRequest();
            isThisUserJoined=1;

        } else if(isThisUserJoined==1 || isThisUserJoined==2){
            ActivityRequest activityRequest = activityRequesRepository.findByActivityAndApplicant(loggedUser, activity);
            //    userEventService.removeApprovedRequestPoints(activityRequest);


            if (activity.getDeadLine().compareTo(DateUtil.xHoursLater(2)) < 0)
                throw new UserWarningException("Son 2 saatte isteği iptal edemezsin");

            activityRequesRepository.delete(activityRequest);
            isThisUserJoined=0;

            //delete points if this activity request was approved
            vibeService.deleteVotesOfNonComingUser(activityRequest.getActivity(),loggedUser);
        }

       return isThisUserJoined;
    }


    //this is used for messaging anc message_activity
    public Boolean isThisUserApprovedTwoDaysLimit(Activity activity) {

        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (u.getId() == 3211)
            return true;

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DATE, -2);

        if (activity.getDeadLine().compareTo(now.getTime()) < 0)
            return false;

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (activity.getCreator().getId() == loggedUser.getId())
            return true;

        Boolean isThisUserApproved = false;
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(activity.getId());
        for (ActivityRequest activityRequest : activityRequests) {
            if (activityRequest.getApplicant().getId() == loggedUser.getId() && activityRequest.getActivityRequestStatus().equals(ActivityRequestStatus.APPROVED)) {
                isThisUserApproved = true;
            }
        }
        return isThisUserApproved;
    }

    public Boolean isThisUserApprovedAllTimes(Activity activity) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (activity.getCreator().getId() == loggedUser.getId())
            return true;

        if(premiumService.userPremiumType(loggedUser)=="GOLD" || premiumService.userPremiumType(loggedUser)=="ORGANIZATOR")
            return true;

        Boolean isThisUserApproved = false;
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(activity.getId());
        for (ActivityRequest activityRequest : activityRequests) {
            if (activityRequest.getApplicant().getId() == loggedUser.getId() && activityRequest.getActivityRequestStatus().equals(ActivityRequestStatus.APPROVED)) {
                isThisUserApproved = true;
            }
        }
        return isThisUserApproved;
    }

    public ActivityRequestStatus approveRequest(Long id) {
        ActivityRequest activityRequest = activityRequesRepository.findById(id).get();

        //check user owner
        UserUtil.checkUserOwner(activityRequest.getActivity().getCreator().getId());

        //check Activity time
        Date now = new Date();
        if (activityRequest.getActivity().getDeadLine().compareTo(now) < 0) {
            throw new UserWarningException("Tarihi geçmiş aktivitede değişiklik yapamazsın");
        }


        if (blockService.isThereABlock(activityRequest.getActivity().getCreator().getId()))
            throw new UserWarningException("Erişim  yok");


        if (activityRequest.getActivityRequestStatus() == ActivityRequestStatus.WAITING) {
            checkMaxApproveCountExceeded(activityRequest.getActivity());
            activityRequest.setActivityRequestStatus(ActivityRequestStatus.APPROVED);
            activityRequest.setResult(1);
            activityRequesRepository.save(activityRequest);
            userEventService.newApproval(activityRequest.getApplicant(), activityRequest.getActivity());
        } else {
            activityRequest.setResult(null);
            activityRequest.setActivityRequestStatus(ActivityRequestStatus.WAITING);
            activityRequesRepository.save(activityRequest);
            vibeService.deleteVotesOfNonComingUser(activityRequest.getActivity(),activityRequest.getApplicant());
        }

        return activityRequest.getActivityRequestStatus();
    }


    public Integer isThisUserJoined(Long meetingId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer isThisUserJoined = 0;
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(meetingId);
        for (ActivityRequest activityRequest : activityRequests) {
            if (activityRequest.getApplicant().getId() == loggedUser.getId()) {
                isThisUserJoined = 1;
                if(activityRequest.getActivityRequestStatus()==ActivityRequestStatus.APPROVED) {
                    isThisUserJoined = 2;
                }
                break;
            }

        }
        return isThisUserJoined;
    }

    public void checkMaxApproveCountExceeded(Activity activity) {

        if (activity.getCreator().getId() == 3212 || activity.getCreator().getId() == 448) {
            return;
        }

        Integer c = activityRequesRepository.countOfAprrovedForThisActivity(activity, ActivityRequestStatus.APPROVED);
        User user = activity.getCreator();


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

    public List<User> findAttendantEntities(Activity activity) {
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(activity.getId());

        List<User> attendantUsers = new ArrayList<>();
        for (ActivityRequest request : activityRequests) {
            if (request.getActivityRequestStatus() == ActivityRequestStatus.APPROVED)
                attendantUsers.add(request.getApplicant());
        }

        return attendantUsers;
    }

    public void deleteByActivityId(Long id) {
        for (ActivityRequest activityRequest : activityRequesRepository.findByActivityId(id)) {
            activityRequesRepository.delete(activityRequest);
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
        Integer user1host = activityRequesRepository.haveUser1HostUser2(user1, user2, ActivityRequestStatus.APPROVED, now.getTime());
        Integer user2host = activityRequesRepository.haveUser1HostUser2(user2, user1, ActivityRequestStatus.APPROVED, now.getTime());

        if (user1host > 0 || user2host > 0)
            return true;


        //if they hosted by same activity
        List<Activity> activityList1 = activityRequesRepository.activitiesAttendedByUser(user1, ActivityRequestStatus.APPROVED);
        List<Activity> activityList2 = activityRequesRepository.activitiesAttendedByUser(user2, ActivityRequestStatus.APPROVED);


        for (Activity a1 : activityList1) {
            for (Activity a2 : activityList2) {
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
        Integer user1host = activityRequesRepository.haveUser1HostUser2AllTimes(user1, user2, ActivityRequestStatus.APPROVED);
        Integer user2host = activityRequesRepository.haveUser1HostUser2AllTimes(user2, user1, ActivityRequestStatus.APPROVED);

        if (user1host > 0 || user2host > 0)
            return true;


        //if they hosted by same activity
        List<Activity> activityList1 = activityRequesRepository.activitiesAttendedByUser(user1, ActivityRequestStatus.APPROVED);
        List<Activity> activityList2 = activityRequesRepository.activitiesAttendedByUser(user2, ActivityRequestStatus.APPROVED);


        for (Activity a1 : activityList1) {

            if(a1.getCreator().getId()==3212 || a1.getCreator().getId()==448)
                continue;

            for (Activity a2 : activityList2) {
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
