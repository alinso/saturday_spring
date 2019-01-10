package com.alinso.myapp.service;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.MeetingRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MeetingRepository;
import com.alinso.myapp.repository.MeetingRequesRepository;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MeetingRequestService {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    MeetingRequesRepository meetingRequesRepository;

    @Autowired
    BlockService blockService;

    public Boolean sendRequest(Long id) {
        Meeting meeting = meetingRepository.findById(id).get();
        if(meeting.getDeadLine().compareTo(new Date())<0)
            throw new UserWarningException("Geçmiş tarihli bir aktivitede düzenleme yapamazsınız");

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(blockService.isBlockedByIt(meeting.getCreator().getId()))
            throw new UserWarningException("Engellendiniz");


        Boolean isThisUserJoined = isThisUserJoined(meeting.getId());
        if(!isThisUserJoined){
            MeetingRequest newMeetingRequest  =new MeetingRequest();
            newMeetingRequest.setApplicant(loggedUser);
            newMeetingRequest.setMeeting(meeting);
            newMeetingRequest.setMeetingRequestStatus(MeetingRequestStatus.WAITING);
            meetingRequesRepository.save(newMeetingRequest);
            userEventService.newRequest(meeting.getCreator(),meeting.getId());
        }
        else{
            MeetingRequest meetingRequest  =meetingRequesRepository.findByMeetingaAndApplicant(loggedUser,meeting);
            meetingRequesRepository.delete(meetingRequest);
        }

        //we have changed the status in above if-else condition
        //so we need to return opposite of initial value
        return !isThisUserJoined;
    }


    public MeetingRequestStatus approveRequest(Long id) {
        MeetingRequest meetingRequest = meetingRequesRepository.findById(id).get();

        //check user owner
        UserUtil.checkUserOwner(meetingRequest.getMeeting().getCreator().getId());


        if(blockService.isBlockedByIt(meetingRequest.getMeeting().getCreator().getId()))
            throw new UserWarningException("Engellendiniz");


        if(meetingRequest.getMeetingRequestStatus()==MeetingRequestStatus.WAITING){
            checkMaxApproveCountExceeded(meetingRequest.getMeeting());
            meetingRequest.setMeetingRequestStatus(MeetingRequestStatus.APPROVED);
            meetingRequesRepository.save(meetingRequest);
            userEventService.newApproval(meetingRequest.getApplicant(),meetingRequest.getMeeting().getId());
        }
        else{
            meetingRequest.setMeetingRequestStatus(MeetingRequestStatus.WAITING);
            meetingRequesRepository.save(meetingRequest);
            userEventService.removeMeeting(meetingRequest.getApplicant());

        }

        return meetingRequest.getMeetingRequestStatus();
    }


    public Boolean isThisUserJoined(Long meetingId){
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isThisUserJoined=false;
        List<MeetingRequest> meetingRequests  =meetingRequesRepository.findByMeetingId(meetingId);
        for(MeetingRequest meetingRequest:meetingRequests){
            if(meetingRequest.getApplicant().getId()==loggedUser.getId()){
                isThisUserJoined=true;
            }
        }
        return isThisUserJoined;
    }

    public void checkMaxApproveCountExceeded(Meeting meeting){
        Integer c= meetingRequesRepository.countOfAprrovedForThisMeetingId(meeting, MeetingRequestStatus.APPROVED);
        if(c==2){
            throw  new UserWarningException("Her aktivite için en fazla 2 kişi onaylayabilirsiniz");
        }
    }

    public List<ProfileDto> findAttendants(Meeting meeting){
        List<MeetingRequest> meetingRequests  = meetingRequesRepository.findByMeetingId(meeting.getId());

        List<User> attendantUsers = new ArrayList<>();
        for(MeetingRequest request: meetingRequests){
            if(request.getMeetingRequestStatus()==MeetingRequestStatus.APPROVED)
                attendantUsers.add(request.getApplicant());
        }

        List<ProfileDto> profileDtos = new ArrayList<>();
        for(User user: attendantUsers){
            profileDtos.add(modelMapper.map(user,ProfileDto.class));
        }
        profileDtos.add(modelMapper.map(meeting.getCreator(), ProfileDto.class));
        return profileDtos;
    }

    public void deleteByMeetingId(Long id){
        for(MeetingRequest meetingRequest:meetingRequesRepository.findByMeetingId(id)){
            meetingRequesRepository.delete(meetingRequest);
        }
    }

}
