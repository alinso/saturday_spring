package com.alinso.myapp.service;

import com.alinso.myapp.dto.meeting.MeetingDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MeetingRepository;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingService {

    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    ModelMapper modelMapper;


    public MeetingDto findById(Long id){
        Meeting meeting =  meetingRepository.getOne(id);
        ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);

        MeetingDto meetingDto  =modelMapper.map(meeting,MeetingDto.class);
        meetingDto.setProfileDto(profileDto);

        return meetingDto;

    }


    public Meeting save(Meeting meeting){

        User creator = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        meeting.setCreator(creator);

        return  meetingRepository.save(meeting);
    }

    public Meeting update(Meeting meeting){
        Meeting meetingInDb  =meetingRepository.findById(meeting.getId()).get();

        User creator = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(meetingInDb.getCreator().getId()!=creator.getId()){
            throw  new UserWarningException("Bu buluşmayı düzenleyemezsiniz");
        }


        meetingInDb.setDetail(meeting.getDetail());
        return meetingRepository.save(meetingInDb);
    }

    public List<MeetingDto> findAll(){
        List<Meeting> meetings = meetingRepository.findAllByOrderByIdDesc();
        List<MeetingDto> meetingDtos = new ArrayList<>();
        for(Meeting meeting : meetings){

            ProfileDto profileDto = modelMapper.map(meeting.getCreator(), ProfileDto.class);
            profileDto.setAge(UserUtil.calculateAge(meeting.getCreator()));

            MeetingDto meetingDto = modelMapper.map(meeting, MeetingDto.class);
            meetingDto.setProfileDto(profileDto);
            meetingDtos.add(meetingDto);
        }
        return meetingDtos;
    }


    public void deleteById(Long id) {
        User user  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Meeting meeting = meetingRepository.getOne(id);
        if(user.getId()!=meeting.getCreator().getId()) {
            throw new UserWarningException("Bunu silmeye yetkiniz yok");
        }

        meetingRepository.deleteById(id);
    }
}
