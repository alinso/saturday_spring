package com.alinso.myapp.service;

import com.alinso.myapp.dto.meeting.MeetingDto;
import com.alinso.myapp.dto.meeting.MeetingRequestDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.MeetingRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MeetingRepository;
import com.alinso.myapp.repository.MeetingRequesRepository;
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

@Service
public class MeetingService {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MeetingRequesRepository meetingRequesRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    UserStatsService userStatsService;

    @Autowired
    CityService cityService;


    @Value("${upload.path}")
    private String fileUploadPath;

    private Boolean isThisUserJoined(Long meetingId){
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

    private void checkMaxApproveCountExceeded(Meeting meeting){
        Integer c= meetingRequesRepository.countOfAprrovedForThisMeetingId(meeting,MeetingRequestStatus.APPROVED);
        if(c==2){
            throw  new UserWarningException("Her aktivite için en fazla 2 kişi onaylayabilirsiniz");
        }
    }


    public MeetingDto findById(Long id) {
        Meeting meeting = meetingRepository.getOne(id);

        UserUtil.checkUserOwner(meeting.getCreator().getId());

        ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);

        MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
        meetingDto.setProfileDto(profileDto);
        meetingDto.setDeadLineString(DateUtil.dateToString(meeting.getDeadLine(),"dd/MM/yyyy HH:mm"));
        meetingDto.setThisUserJoined(isThisUserJoined(meeting.getId()));


        return meetingDto;

    }


    public Meeting save(MeetingDto meetingDto) {

        Meeting meeting = new Meeting(meetingDto.getDetail());
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        City city = cityService.findById(meetingDto.getCityId());

        meeting.setCity(city);
        meeting.setCreator(loggedUser);
        meeting.setDeadLine(DateUtil.stringToDate(meetingDto.getDeadLineString(),"dd/MM/yyyy HH:mm"));
        meeting.setPhotoName(fileStorageUtil.saveFileAndReturnName(meetingDto.getFile(),fileUploadPath));
        userStatsService.newMeeting(loggedUser);

        return meetingRepository.save(meeting);
    }

    public Meeting update(MeetingDto meetingDto) {

        Meeting meetingInDb = meetingRepository.findById(meetingDto.getId()).get();

        //check user owner
        UserUtil.checkUserOwner(meetingInDb.getCreator().getId());

        City city = cityService.findById(meetingDto.getCityId());
        meetingInDb.setCity(city);


        //save new photo and remove old one
        if (meetingDto.getFile() != null) {
            fileStorageUtil.deleteFile(fileUploadPath + meetingInDb.getPhotoName());
            meetingInDb.setPhotoName(fileStorageUtil.saveFileAndReturnName(meetingDto.getFile(),fileUploadPath));
        }

        meetingInDb.setDeadLine(DateUtil.stringToDate(meetingDto.getDeadLineString(),"dd/MM/yyyy HH:mm"));
        meetingInDb.setDetail(meetingDto.getDetail());
        return meetingRepository.save(meetingInDb);
    }

    public List<MeetingDto> findAllNonExpiredByCityId(Long cityId) {

        List<Meeting> meetings = meetingRepository.findAllNonExpiredByCityId(new Date(),cityService.findById(cityId));
        List<MeetingDto> meetingDtos = new ArrayList<>();

        for (Meeting meeting : meetings) {

            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
            meetingDto.setThisUserJoined(isThisUserJoined(meeting.getId()));
            meetingDto.setDeadLineString(DateUtil.dateToString(meeting.getDeadLine(),"dd/MM/yyyy HH:mm"));
            meetingDto.setProfileDto(profileDto);
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }


    public void deleteById(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Meeting meetingInDb = meetingRepository.findById(id).get();

        //check user authorized
        UserUtil.checkUserOwner(meetingInDb.getCreator().getId());

        //decrease user's meeting count
        userStatsService.removeMeeting(user);

        //delete file
        fileStorageUtil.deleteFile(fileUploadPath + meetingInDb.getPhotoName());

        //delete requests
        for(MeetingRequest meetingRequest:meetingRequesRepository.findByMeetingId(id)){
            meetingRequesRepository.delete(meetingRequest);
        }

        meetingRepository.deleteById(id);
    }

    public List<MeetingDto> meetingsOfUser(Long id) {
        User user = userRepository.findById(id).get();
        List<Meeting> meetingsCreatedByUser = meetingRepository.findByCreatorOrderByIdDesc(user);
        List <Meeting> meetingsAttendedByUser  =meetingRequesRepository.meetingsAttendedByUser(user,MeetingRequestStatus.APPROVED);

        List<Meeting> meetings = new ArrayList<>();
        meetings.addAll(meetingsCreatedByUser);
        meetings.addAll(meetingsAttendedByUser);

        List<MeetingDto> meetingDtos = new ArrayList<>();
        for (Meeting meeting : meetings) {

            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
            meetingDto.setDeadLineString(DateUtil.dateToString(meeting.getDeadLine(),"dd/MM/yyyy hh:mm"));
            if(meeting.getDeadLine().compareTo(new Date()) < 0)
                meetingDto.setExpired(true);
            else
                meetingDto.setExpired(false);

            meetingDto.setThisUserJoined(isThisUserJoined(meeting.getId()));
            meetingDto.setProfileDto(profileDto);
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }

    public Boolean join(Long id) {
        Meeting meeting = meetingRepository.findById(id).get();
        if(meeting.getDeadLine().compareTo(new Date())<0)
            throw new UserWarningException("Geçmiş tarihli bir aktivitede düzenleme yapamazsınız");

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean isThisUserJoined = isThisUserJoined(meeting.getId());
        if(!isThisUserJoined){
            MeetingRequest newMeetingRequest  =new MeetingRequest();
            newMeetingRequest.setApplicant(loggedUser);
            newMeetingRequest.setMeeting(meeting);
            newMeetingRequest.setMeetingRequestStatus(MeetingRequestStatus.WAITING);
            meetingRequesRepository.save(newMeetingRequest);
        }
        else{
            MeetingRequest meetingRequest  =meetingRequesRepository.findByMeetingaAndApplicant(loggedUser,meeting);
            meetingRequesRepository.delete(meetingRequest);
        }

        //we have changed the status in above if-else condition
        //so we need to return opposite of initial value
        return !isThisUserJoined;
    }

    public MeetingDto getMeetingWithRequests(Long id) {

        List<MeetingRequest> meetingRequests  =meetingRequesRepository.findByMeetingId(id);
        Meeting meeting  =meetingRepository.findById(id).get();

        List<MeetingRequestDto> meetingRequestDtos =  new ArrayList<>();
        for(MeetingRequest meetingRequest :meetingRequests){
            MeetingRequestDto meetingRequestDto  = modelMapper.map(meetingRequest,MeetingRequestDto.class);

            ProfileDto profileDto  =modelMapper.map(meetingRequest.getApplicant(),ProfileDto.class);
            Integer age = UserUtil.calculateAge(meetingRequest.getApplicant());
            profileDto.setAge(age);

            meetingRequestDto.setProfileDto(profileDto);
            meetingRequestDtos.add(meetingRequestDto);
        }

        MeetingDto meetingDto = modelMapper.map(meeting,MeetingDto.class);
        meetingDto.setDeadLineString(DateUtil.dateToString(meeting.getDeadLine(),"dd/MM/yyyy hh:mm"));
        meetingDto.setRequests(meetingRequestDtos);

        return meetingDto;
    }

    public MeetingRequestStatus approveRequest(Long id) {
        MeetingRequest meetingRequest = meetingRequesRepository.findById(id).get();

        //check user owner
        UserUtil.checkUserOwner(meetingRequest.getMeeting().getCreator().getId());


        if(meetingRequest.getMeetingRequestStatus()==MeetingRequestStatus.WAITING){
            checkMaxApproveCountExceeded(meetingRequest.getMeeting());
            meetingRequest.setMeetingRequestStatus(MeetingRequestStatus.APPROVED);
            meetingRequesRepository.save(meetingRequest);
            userStatsService.newMeeting(meetingRequest.getApplicant());
        }
        else{
            meetingRequest.setMeetingRequestStatus(MeetingRequestStatus.WAITING);
            meetingRequesRepository.save(meetingRequest);
            userStatsService.removeMeeting(meetingRequest.getApplicant());

        }

        return meetingRequest.getMeetingRequestStatus();
    }
}
























