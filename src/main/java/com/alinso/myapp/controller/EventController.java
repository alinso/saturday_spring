package com.alinso.myapp.controller;


import com.alinso.myapp.entity.dto.event.EventDto;
import com.alinso.myapp.service.EventService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.EventValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {


    @Autowired
    EventService eventService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    EventValidator eventValidator;


    @PostMapping("/create")
    public ResponseEntity<?> save(@Valid EventDto eventDto, BindingResult result){

        eventValidator.validate(eventDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        eventService.save(eventDto);
        return new ResponseEntity<>(eventDto,HttpStatus.ACCEPTED);
    }

    @GetMapping("findAllByCityId/{cityId}/{pageNum}")
    public ResponseEntity<?> findAll(@PathVariable("cityId") Long cityId, @PathVariable("pageNum") Integer pageNum){
        List<EventDto>  meetings = eventService.findAllNonExpiredByCityId(cityId,pageNum);

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }
    @GetMapping("findByInterestByCityId/{cityId}/{pageNum}")
    public ResponseEntity<?> findByInterestByCityId(@PathVariable("cityId") Long cityId, @PathVariable("pageNum") Integer pageNum){
        List<EventDto>  events = eventService.findAllNonExpiredByInterestsByCityId(cityId,pageNum);

        return new ResponseEntity<>(events,HttpStatus.OK);
    }

    @GetMapping("eventWithRequests/{id}")
    public ResponseEntity<?> eventWithRequests(@PathVariable("id") Long id){
        EventDto eventDto = eventService.getEventWithRequests(id);
        return new ResponseEntity<>(eventDto,HttpStatus.OK);
    }


    @GetMapping("findByUserId/{id}/{pageNum}/{type}")
    public ResponseEntity<?> findByUserIdType(@PathVariable("id") Long id,@PathVariable("pageNum") Integer pageNum, @PathVariable("type") String type){
        List<EventDto>  eventDtos = eventService.eventsOfUser(id,pageNum,type);

        return new ResponseEntity<>(eventDtos,HttpStatus.OK);
    }
    @GetMapping("findByUserId/{id}/{pageNum}")
    public ResponseEntity<?> findByUserId(@PathVariable("id") Long id, @PathVariable("pageNum") Integer pageNum){
        List<EventDto>  events = eventService.allEventssOfUser(id,pageNum);

        return new ResponseEntity<>(events,HttpStatus.OK);
    }

    @GetMapping("all/{pageNum}")
    public ResponseEntity<?> all(@PathVariable("pageNum") Integer pageNum){
        List<EventDto> eventDtos = eventService.all(pageNum);
        return new ResponseEntity<>(eventDtos,HttpStatus.OK);
    }

    @GetMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        eventService.deleteById(id);

        return new ResponseEntity<>("deleted",HttpStatus.OK);
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@Valid EventDto eventDto, BindingResult result){

        eventValidator.validate(eventDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;


        eventService.update(eventDto);

        return new ResponseEntity<>(eventDto,HttpStatus.ACCEPTED);

    }

    @GetMapping("findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){

        EventDto eventDto = eventService.findById(id);

        return new ResponseEntity<>(eventDto,HttpStatus.OK);
    }




}
