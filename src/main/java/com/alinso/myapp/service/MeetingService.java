package com.alinso.myapp.service;

import com.alinso.myapp.dto.meeting.MeetingDto;
import com.alinso.myapp.dto.meeting.MeetingRequestDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.MeetingRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MeetingRepository;
import com.alinso.myapp.repository.MeetingRequesRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        meetingDto.setThisUserJoined(isThisUserJoined(meeting.getId()));


        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String birthDateString = format.format(meeting.getUpdatedAt());
        meetingDto.setUpdatedAt(birthDateString);


        return meetingDto;

    }


    public Meeting save(MeetingDto meetingDto) {

        Meeting meeting = new Meeting(meetingDto.getDetail());

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User creator = userRepository.findById(loggedUser.getId()).get();

        meeting.setCreator(creator);

        //save new photo
        String newName = null;
        if (meetingDto.getFile() != null) {
            String extension = FilenameUtils.getExtension(meetingDto.getFile().getOriginalFilename());
            newName = fileStorageUtil.makeFileName() + "." + extension;
            fileStorageUtil.storeFile(meetingDto.getFile(), fileUploadPath, newName);
        }
        meeting.setPhotoName(newName);

        //update users event Count
        creator.setMeetingCount((creator.getMeetingCount() + 1));


        return meetingRepository.save(meeting);
    }

    public Meeting update(MeetingDto meetingDto) {

        Meeting meetingInDb = meetingRepository.findById(meetingDto.getId()).get();

        //check user owner
        UserUtil.checkUserOwner(meetingInDb.getCreator().getId());


        //save new photo and remove old one
        String newName = null;
        if (meetingDto.getFile() != null) {
            String extension = FilenameUtils.getExtension(meetingDto.getFile().getOriginalFilename());
            newName = fileStorageUtil.makeFileName() + "." + extension;
            fileStorageUtil.storeFile(meetingDto.getFile(), fileUploadPath, newName);
            fileStorageUtil.deleteFile(fileUploadPath + meetingInDb.getPhotoName());
            meetingInDb.setPhotoName(newName);
        }
        meetingInDb.setDetail(meetingDto.getDetail());
        return meetingRepository.save(meetingInDb);
    }

    public List<MeetingDto> findAll() {
        List<Meeting> meetings = meetingRepository.findAllByOrderByIdDesc();
        List<MeetingDto> meetingDtos = new ArrayList<>();


        for (Meeting meeting : meetings) {
            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
            meetingDto.setThisUserJoined(isThisUserJoined(meeting.getId()));

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            String birthDateString = format.format(meeting.getUpdatedAt());
            meetingDto.setUpdatedAt(birthDateString);


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
        Integer meetingCount = user.getMeetingCount()-1;
        user.setMeetingCount(meetingCount);
        userRepository.save(user);

        //delete file
        fileStorageUtil.deleteFile(fileUploadPath + meetingInDb.getPhotoName());

        //delete requests
        for(MeetingRequest meetingRequest:meetingRequesRepository.findByMeetingId(id)){
            meetingRequesRepository.delete(meetingRequest);
        }

        meetingRepository.deleteById(id);
    }

    public List<MeetingDto> findByUserId(Long id) {

        User user = userRepository.findById(id).get();

        List<Meeting> meetings = meetingRepository.findByCreatorOrderByIdDesc(user);
        List<MeetingDto> meetingDtos = new ArrayList<>();
        for (Meeting meeting : meetings) {

            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            String birthDateString = format.format(meeting.getUpdatedAt());
            meetingDto.setUpdatedAt(birthDateString);
            meetingDto.setThisUserJoined(isThisUserJoined(meeting.getId()));

            meetingDto.setProfileDto(profileDto);
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }

    public Boolean join(Long id) {
        Meeting meeting = meetingRepository.findById(id).get();
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

        MeetingDto meetingDto = modelMapper.map(meeting,MeetingDto.class);

        List<MeetingRequestDto> meetingRequestDtos =  new ArrayList<>();
        for(MeetingRequest meetingRequest :meetingRequests){
            MeetingRequestDto meetingRequestDto  = modelMapper.map(meetingRequest,MeetingRequestDto.class);

            ProfileDto profileDto  =modelMapper.map(meetingRequest.getApplicant(),ProfileDto.class);
            Integer age = UserUtil.calculateAge(meetingRequest.getApplicant());
            profileDto.setAge(age);

            meetingRequestDto.setProfileDto(profileDto);
            meetingRequestDtos.add(meetingRequestDto);
        }

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
        }
        else{
            meetingRequest.setMeetingRequestStatus(MeetingRequestStatus.WAITING);
            meetingRequesRepository.save(meetingRequest);
        }

        return meetingRequest.getMeetingRequestStatus();
    }
}
























