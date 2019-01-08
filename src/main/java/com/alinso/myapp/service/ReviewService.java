package com.alinso.myapp.service;

import com.alinso.myapp.dto.review.ReviewDto;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.MeetingRequest;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.MeetingRequestStatus;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.MeetingRepository;
import com.alinso.myapp.repository.MeetingRequesRepository;
import com.alinso.myapp.repository.ReviewRepository;
import com.alinso.myapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MeetingRequesRepository meetingRequesRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserStatsService userStatsService;

    private final Integer DAYS_TO_WRITE_REVIEW = -10;
    private final Integer HOURS_TO_WRITE_REVIEW = -1;


    public Boolean haveUserAttendMeeting(Meeting meeting, User user){
        Boolean result = false;
        MeetingRequest myRequest = new MeetingRequest();
        try {
            myRequest = meetingRequesRepository.findByMeetingaAndApplicant(user, meeting);

            //I have attend one of his meetings
            if (myRequest.getMeetingRequestStatus() == MeetingRequestStatus.APPROVED) {
                result = true;
            }
        } catch (Exception e) {
            //do nothing
        }
        return result;
    }


    public Boolean haveUsersMeetRecently(User me, User other) {

        Calendar start = Calendar.getInstance();
        start.setTime(new Date());
        start.add(Calendar.DATE, DAYS_TO_WRITE_REVIEW);

        Calendar finish = Calendar.getInstance();
        finish.setTime(new Date());
        finish.add(Calendar.HOUR, HOURS_TO_WRITE_REVIEW);


        List<Meeting> recentMeetingsCreatedByMe = meetingRepository.recentMeetingsOfCreator(start.getTime(), finish.getTime(), me);
        List<Meeting> recentMeetingsCreatedByOther = meetingRepository.recentMeetingsOfCreator(start.getTime(), finish.getTime(), other);


        //let say users havent meet initially
        Boolean result = false;

        //have I attend his meeting?
        for (Meeting meeting : recentMeetingsCreatedByOther) {
            if(haveUserAttendMeeting(meeting,me)) {
                result = true;
                break;
            }
        }

        //has he attend my meeting
        //if I already attend one of him no need to check this
        if(!result)
        for (Meeting meeting : recentMeetingsCreatedByMe) {
            if(haveUserAttendMeeting(meeting,other)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void writeReview(ReviewDto reviewDto) {
        User reader = userRepository.findById(reviewDto.getReader().getId()).get();
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(reviewDto.getReviewType()==ReviewType.MEETING && !haveUsersMeetRecently(writer, reader))
            throw new UserWarningException("Daha önce bir aktiviteye katılmadınız!");

        if(isReviewedBefore(reviewDto.getReader().getId()))
            throw new UserWarningException("Bir kişi için daha önce referans yazdınız");

        userStatsService.referenceWritten(reader, reviewDto.getReviewType(),reviewDto.getPositive());


        Review review = modelMapper.map(reviewDto, Review.class);
        review.setWriter(writer);
        review.setReader(reader);
        review.setReviewType(ReviewType.FRIEND);

        reviewRepository.save(review);

    }


    public Boolean isReviewedBefore(Long id) {
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User other = userRepository.findById(id).get();

        Boolean isReviewedBefore = true;
        Review myPreviousReview = reviewRepository.myPreviousReview(me, other);
        if(myPreviousReview==null)
            isReviewedBefore=false;

        return isReviewedBefore;
    }

    public List<ReviewDto> reviewsOfUser(Long id) {
        User user  =userRepository.findById(id).get();
        List<Review> reviews = reviewRepository.findByReader(user);

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for(Review review : reviews){
            ReviewDto reviewDto  = modelMapper.map(review,ReviewDto.class);
            reviewDtos.add(reviewDto);
        }
        return  reviewDtos;
    }
}
