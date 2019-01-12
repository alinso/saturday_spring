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
    MeetingRequestService meetingRequestService;

    @Autowired
    MeetingRequesRepository meetingRequesRepository;

    @Autowired
    BlockService blockService;

    @Value("${upload.path}")
    private String fileUploadPath;


    public MeetingDto findById(Long id) {
        Meeting meeting = meetingRepository.findById(id).get();

        ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);

        MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
        meetingDto.setProfileDto(profileDto);
        meetingDto.setDeadLineString(DateUtil.dateToString(meeting.getDeadLine(),"dd/MM/yyyy HH:mm"));
        meetingDto.setThisUserJoined(meetingRequestService.isThisUserJoined(meeting.getId()));
        meetingDto.setAttendants(meetingRequestService.findAttendants(meeting));

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
        userEventService.newMeeting(loggedUser);

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

        List<Meeting> meetings = meetingRepository.findAllNonExpiredByCityIdOrderByDeadLine(new Date(),cityService.findById(cityId));
        List<MeetingDto> meetingDtos = new ArrayList<>();

        for (Meeting meeting : meetings) {

            if(blockService.isThereABlock(meeting.getCreator().getId()))
                continue;

            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
            meetingDto.setThisUserJoined(meetingRequestService.isThisUserJoined(meeting.getId()));
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
        userEventService.removeMeeting(user);

        //delete file
        fileStorageUtil.deleteFile(fileUploadPath + meetingInDb.getPhotoName());

        //delete requests
       meetingRequestService.deleteByMeetingId(id);

        meetingRepository.deleteById(id);
    }

    public List<MeetingDto> meetingsOfUser(Long id) {
        User user = userRepository.findById(id).get();


        if(blockService.isThereABlock(id))
            throw  new UserWarningException("Erişim Yok");

        List<Meeting> meetingsCreatedByUser = meetingRepository.findByCreatorOrderByDeadLineDesc(user);
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

            meetingDto.setAttendants(meetingRequestService.findAttendants(meeting));
            meetingDto.setThisUserJoined(meetingRequestService.isThisUserJoined(meeting.getId()));
            meetingDto.setProfileDto(profileDto);
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }

    public MeetingDto getMeetingWithRequests(Long id) {

        List<MeetingRequest> meetingRequests  =meetingRequesRepository.findByMeetingId(id);
        Meeting meeting  =meetingRepository.findById(id).get();

        UserUtil.checkUserOwner(meeting.getCreator().getId());

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
}
























