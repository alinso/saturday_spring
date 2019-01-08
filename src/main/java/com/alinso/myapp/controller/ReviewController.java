package com.alinso.myapp.controller;

import com.alinso.myapp.dto.review.ReviewDto;
import com.alinso.myapp.service.ReviewService;
import com.alinso.myapp.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/review")
@RestController
public class ReviewController {

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    ReviewService reviewService;

    @PostMapping("writeReview/")
    public ResponseEntity<?> writeReview(@Valid @RequestBody ReviewDto reviewDto, BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        reviewService.writeReview(reviewDto);
        return new ResponseEntity<>(reviewDto, HttpStatus.ACCEPTED);
    }


    @GetMapping("/isReviewedBefore/{id}")
    public ResponseEntity<?> isReviewedBefore(@PathVariable("id") Long id){
        Boolean result  =reviewService.isReviewedBefore(id);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @GetMapping("reviewsOfUser/{id}")
    public ResponseEntity<?> reviewsOfUser(@PathVariable("id") Long id){
        List<ReviewDto> reviews  = reviewService.reviewsOfUser(id);
        return new ResponseEntity<>(reviews,HttpStatus.OK);
    }

}
