package com.alinso.myapp.service;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivityRequestService {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    BlockService blockService;

    public Boolean sendRequest(Long id) {
        Activity activity = activityRepository.findById(id).get();
        if(activity.getDeadLine().compareTo(new Date())<0)
            throw new UserWarningException("Geçmiş tarihli bir aktivitede düzenleme yapamazsınız");

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(blockService.isThereABlock(activity.getCreator().getId()))
            throw new UserWarningException("Erişim Yok");


        Boolean isThisUserJoined = isThisUserJoined(activity.getId());
        if(!isThisUserJoined){
            ActivityRequest newActivityRequest =new ActivityRequest();
            newActivityRequest.setApplicant(loggedUser);
            newActivityRequest.setActivity(activity);
            newActivityRequest.setActivityRequestStatus(ActivityRequestStatus.WAITING);
            activityRequesRepository.save(newActivityRequest);
            userEventService.newRequest(activity.getCreator(), activity.getId());
        }
        else{
            ActivityRequest activityRequest = activityRequesRepository.findByActivityAndApplicant(loggedUser, activity);
            activityRequesRepository.delete(activityRequest);
        }

        //we have changed the status in above if-else condition
        //so we need to return opposite of initial value
        return !isThisUserJoined;
    }


    public ActivityRequestStatus approveRequest(Long id) {
        ActivityRequest activityRequest = activityRequesRepository.findById(id).get();

        //check user owner
        UserUtil.checkUserOwner(activityRequest.getActivity().getCreator().getId());


        if(blockService.isThereABlock(activityRequest.getActivity().getCreator().getId()))
            throw new UserWarningException("Erişim  yok");


        if(activityRequest.getActivityRequestStatus()== ActivityRequestStatus.WAITING){
            checkMaxApproveCountExceeded(activityRequest.getActivity());
            activityRequest.setActivityRequestStatus(ActivityRequestStatus.APPROVED);
            activityRequesRepository.save(activityRequest);
            userEventService.newApproval(activityRequest.getApplicant(), activityRequest.getActivity());
        }
        else{
            activityRequest.setActivityRequestStatus(ActivityRequestStatus.WAITING);
            activityRequesRepository.save(activityRequest);
            userEventService.cancelApproval(activityRequest.getApplicant(),activityRequest.getActivity());
        }

        return activityRequest.getActivityRequestStatus();
    }


    public Boolean isThisUserJoined(Long meetingId){
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isThisUserJoined=false;
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(meetingId);
        for(ActivityRequest activityRequest : activityRequests){
            if(activityRequest.getApplicant().getId()==loggedUser.getId()){
                isThisUserJoined=true;
            }
        }
        return isThisUserJoined;
    }

    public void checkMaxApproveCountExceeded(Activity activity){
        Integer c= activityRequesRepository.countOfAprrovedForThisActivity(activity, ActivityRequestStatus.APPROVED);
        if(c==2){
            throw  new UserWarningException("Her aktivite için en fazla 2 kişi onaylayabilirsiniz");
        }
    }

    public List<ProfileDto> findAttendants(Activity activity){
        List<ActivityRequest> activityRequests = activityRequesRepository.findByActivityId(activity.getId());

        List<User> attendantUsers = new ArrayList<>();
        for(ActivityRequest request: activityRequests){
            if(request.getActivityRequestStatus()== ActivityRequestStatus.APPROVED)
                attendantUsers.add(request.getApplicant());
        }

        List<ProfileDto> profileDtos = new ArrayList<>();
        for(User user: attendantUsers){
                ProfileDto profileDto  =modelMapper.map(user,ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(user));
            profileDtos.add(profileDto);
        }
        ProfileDto creator = modelMapper.map(activity.getCreator(), ProfileDto.class);
        creator.setAge(UserUtil.calculateAge(activity.getCreator()));

        profileDtos.add(creator);

        return profileDtos;
    }

    public void deleteByActivityId(Long id){
        for(ActivityRequest activityRequest : activityRequesRepository.findByActivityId(id)){
            activityRequesRepository.delete(activityRequest);
        }
    }

}
