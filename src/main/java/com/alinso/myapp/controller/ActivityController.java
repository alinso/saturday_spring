package com.alinso.myapp.controller;


import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.dto.activity.ActivityDto;
import com.alinso.myapp.service.ActivityService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import com.alinso.myapp.validator.ActivityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {


    @Autowired
    ActivityService activityService;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    ActivityValidator activityValidator;


    @PostMapping("/create")
    public ResponseEntity<?> save(@Valid ActivityDto activityDto, BindingResult result){

        activityValidator.validate(activityDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        activityService.save(activityDto);
        return new ResponseEntity<>(activityDto,HttpStatus.ACCEPTED);
    }

    @GetMapping("findAllByCityId/{cityId}/{pageNum}")
    public ResponseEntity<?> findAll(@PathVariable("cityId") Long cityId, @PathVariable("pageNum") Integer pageNum){
        List<ActivityDto>  meetings = activityService.findAllNonExpiredByCityId(cityId,pageNum);

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }
    @GetMapping("findByCategoriesByCityId/{cityId}/{pageNum}")
    public ResponseEntity<?> findByCategoriesByCityId(@PathVariable("cityId") Long cityId, @PathVariable("pageNum") Integer pageNum){
        List<ActivityDto>  meetings = activityService.findAllNonExpiredByCategoriesByCityId(cityId,pageNum);

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }

    @GetMapping("activityWithRequests/{id}")
    public ResponseEntity<?> meetingWithRequests(@PathVariable("id") Long id){
        ActivityDto activityDto = activityService.getActivityWithRequests(id);
        return new ResponseEntity<>(activityDto,HttpStatus.OK);
    }


    @GetMapping("findByUserId/{id}/{pageNum}/{type}")
    public ResponseEntity<?> findByUserIdType(@PathVariable("id") Long id,@PathVariable("pageNum") Integer pageNum, @PathVariable("type") String type){
        List<ActivityDto>  meetings = activityService.activitiesOfUser(id,pageNum,type);

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }
    @GetMapping("findByUserId/{id}/{pageNum}")
    public ResponseEntity<?> findByUserId(@PathVariable("id") Long id, @PathVariable("pageNum") Integer pageNum){
        List<ActivityDto>  meetings = activityService.allActivitiesOfUser(id,pageNum);

        return new ResponseEntity<>(meetings,HttpStatus.OK);
    }

    @GetMapping("createdAndJoinedCount/{id}")
    public ResponseEntity<?> createdAndJoinedCount(@PathVariable("id") Long id){
        List<Integer> meetingCounts = activityService.activityCountsOfUser(id);
        return new ResponseEntity<>(meetingCounts,HttpStatus.OK);
    }
    @GetMapping("all/{pageNum}")
    public ResponseEntity<?> all(@PathVariable("pageNum") Integer pageNum){
        List<ActivityDto> activityDtos = activityService.all(pageNum);
        return new ResponseEntity<>(activityDtos,HttpStatus.OK);
    }


    @GetMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        activityService.deleteById(id);

        return new ResponseEntity<>("deleted",HttpStatus.OK);
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@Valid ActivityDto activityDto, BindingResult result){

        activityValidator.validate(activityDto,result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;


        activityService.update(activityDto);

        return new ResponseEntity<>(activityDto,HttpStatus.ACCEPTED);

    }

    @GetMapping("findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){

        ActivityDto activityDto = activityService.findById(id);

        return new ResponseEntity<>(activityDto,HttpStatus.OK);
    }




}
