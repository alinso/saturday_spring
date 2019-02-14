package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.repository.EventRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    ModelMapper modelMapper;


    @Autowired
    EventRepository eventRepository;

    public void save(EventDto eventDto) {

        Event event = modelMapper.map(eventDto,Event.class);
        event.setDate(DateUtil.stringToDate(eventDto.getDtString(), "dd/MM/yyyy HH:mm"));
        event.setPhotoName(fileStorageUtil.saveFileAndReturnName(eventDto.getFile()));

        eventRepository.save(event);
    }

    public List<EventDto> findNonExpiredEvents(){
       List<Event> eventList =  eventRepository.findNonExpiredEvents(new Date());
       List<EventDto> eventDtoList =  new ArrayList<>();
       for(Event event:eventList){
           EventDto eventDto  = modelMapper.map(event,EventDto.class);
           eventDto.setDtString(DateUtil.dateToString(event.getDate(),"dd/MM/yyyy HH:m"));
            eventDtoList.add(eventDto);
        }

        return eventDtoList;
    }
}
