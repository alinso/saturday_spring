package com.alinso.myapp.controller;

import com.alinso.myapp.dto.review.ReviewDto;
import com.alinso.myapp.service.ReviewService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/review")
@RestController
public class ReviewController {

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    ReviewService reviewService;

    @PostMapping("writeAsFriend/")
    public ResponseEntity<?> writeAsFriend(@Valid @RequestBody ReviewDto reviewDto, BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        reviewService.writeAsFriend(reviewDto);
        return new ResponseEntity<>(reviewDto, HttpStatus.ACCEPTED);
    }

}
