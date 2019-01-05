package com.alinso.myapp.service;

import com.alinso.myapp.dto.meeting.MeetingDto;
import com.alinso.myapp.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MeetingRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.util.FileStorageUtil;
import com.alinso.myapp.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Value("${upload.path}")
    private String fileUploadPath;


    public MeetingDto findById(Long id) {
        Meeting meeting = meetingRepository.getOne(id);
        ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
        meetingDto.setProfileDto(profileDto);


        //set meetingDto attendant dtos
        Boolean isThisUserJoins=false;
        List<ProfileDto> attendantDtos =  new ArrayList<>();
        for(User attendant:meeting.getAttendants()){
            if(attendant.getId()==loggedUser.getId()){
                isThisUserJoins=true;
            }
            ProfileDto attendantDto  =modelMapper.map(attendant,ProfileDto.class);
            attendantDtos.add(attendantDto);
        }

        meetingDto.setAttendants(attendantDtos);
        meetingDto.setThisUserJoins(isThisUserJoins);



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

        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (meetingInDb.getCreator().getId() != creator.getId()) {
            throw new UserWarningException("Bu buluşmayı düzenleyemezsiniz");
        }


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
        User loggedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        for (Meeting meeting : meetings) {
            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);

            //set meetingDto attendant dtos
            Boolean isThisUserJoins=false;
            List<ProfileDto> attendantDtos =  new ArrayList<>();
            for(User attendant:meeting.getAttendants()){
                    if(attendant.getId()==loggedUser.getId()){
                        isThisUserJoins=true;
                    }
                    ProfileDto attendantDto  =modelMapper.map(attendant,ProfileDto.class);
                    attendantDtos.add(attendantDto);
            }

            meetingDto.setAttendants(attendantDtos);
            meetingDto.setThisUserJoins(isThisUserJoins);

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

        if (user.getId() != meetingInDb.getCreator().getId()) {
            throw new UserWarningException("Bunu silmeye yetkiniz yok");
        }

        fileStorageUtil.deleteFile(fileUploadPath + meetingInDb.getPhotoName());

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

            meetingDto.setProfileDto(profileDto);
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }

    public Boolean join(Long id) {
        Meeting meeting = meetingRepository.findById(id).get();
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userInDb  =userRepository.findById(loggedUser.getId()).get();


        Boolean isThisUserJoins = true;
        List<User> attendants = meeting.getAttendants();
        for (User attendant : attendants) {
            if (userInDb.getId() == attendant.getId()) {
                isThisUserJoins = false;
            }
        }



        if(isThisUserJoins){
            attendants.add(userInDb);
        }else{
            attendants.remove(userInDb);
        }

        meeting.setAttendants(attendants);
        meetingRepository.save(meeting);
        return isThisUserJoins;
    }
}
























