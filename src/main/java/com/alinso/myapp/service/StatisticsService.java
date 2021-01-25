package com.alinso.myapp.service;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.repository.EventRepository;
import com.alinso.myapp.repository.CityRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StatisticsService {


    @Autowired
    EventRepository eventRepository;


    @Autowired
    UserRepository userRepository;

    @Autowired
    CityRepository cityRepository;

    public Integer aasFemale() {
        Date now = new Date();
        return eventRepository.aasByGender(Gender.FEMALE, now);
    }

    public Integer aasMale() {
        Date now = new Date();
        return eventRepository.aasByGender(Gender.MALE, now);
    }

    public List<Integer> newWomenThreeMonths() {

        Calendar current = Calendar.getInstance();
        current.set(current.get(Calendar.YEAR),current.get(Calendar.MONTH),current.get(Calendar.DATE),0,0,0);
        List<Integer> womanList=new ArrayList<>();

        Integer countToday  =userRepository.userCountCreatedToday(Gender.FEMALE,current.getTime());
        womanList.add(countToday);

        for(int i =0;i<30;i++){
            Date finish=current.getTime();
            current.add(Calendar.DATE,-1);
            Date start=current.getTime();

            Integer count  =userRepository.userCountCreatedGivenDate(Gender.FEMALE,start,finish);
            womanList.add(count);

        }
        return womanList;
    }


    public Integer femaleCount(){
        City ankara= cityRepository.findById(Long.valueOf(1)).get();
        return userRepository.userCountByGenderCityPoint(Gender.FEMALE,ankara,-50);
    }
    public Integer maleCount(){
        City ankara= cityRepository.findById(Long.valueOf(1)).get();
        return userRepository.userCountByGenderCityPoint(Gender.MALE,ankara,-50);
    }

    public Integer activeFemaleCount(){
        City ankara= cityRepository.findById(Long.valueOf(1)).get();
        return userRepository.userCountByGenderCityPoint(Gender.FEMALE,ankara,0);
    }
    public Integer activeMaleCount(){
        City ankara= cityRepository.findById(Long.valueOf(1)).get();
        return userRepository.userCountByGenderCityPoint(Gender.MALE,ankara,0);
    }

    public Integer tooActiveFemaleCount(){
        City ankara= cityRepository.findById(Long.valueOf(1)).get();
        return userRepository.userCountByGenderCityPoint(Gender.FEMALE,ankara,20);
    }
    public Integer tooActiveMaleCount(){
        City ankara= cityRepository.findById(Long.valueOf(1)).get();
        return userRepository.userCountByGenderCityPoint(Gender.MALE,ankara,20);
    }




}
