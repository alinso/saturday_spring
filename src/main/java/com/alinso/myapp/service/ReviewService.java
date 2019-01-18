package com.alinso.myapp.service;

import com.alinso.myapp.dto.review.ReviewDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
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
    ActivityRepository activityRepository;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    BlockService blockService;

    private final Integer DAYS_TO_WRITE_REVIEW = -10;
    private final Integer HOURS_TO_WRITE_REVIEW = -1;


    public Boolean haveUserAttendMeeting(Activity activity, User user) {
        Boolean result = false;
        ActivityRequest myRequest = new ActivityRequest();
        try {
            myRequest = activityRequesRepository.findByActivityAndApplicant(user, activity);

            //I have attend one of his meetings
            if (myRequest.getActivityRequestStatus() == ActivityRequestStatus.APPROVED) {
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


        List<Activity> recentMeetingsCreatedByMe = activityRepository.recentActivitiesOfCreator(start.getTime(), finish.getTime(), me);
        List<Activity> recentMeetingsCreatedByOther = activityRepository.recentActivitiesOfCreator(start.getTime(), finish.getTime(), other);


        //let say users havent meet initially
        Boolean result = false;

        //have I attend his activity?
        for (Activity activity : recentMeetingsCreatedByOther) {
            if (haveUserAttendMeeting(activity, me)) {
                result = true;
                break;
            }
        }

        //has he attend my activity
        //if I already attend one of him no need to check this
        if (!result)
            for (Activity activity : recentMeetingsCreatedByMe) {
                if (haveUserAttendMeeting(activity, other)) {
                    result = true;
                    break;
                }
            }
        return result;
    }

    public void writeReview(ReviewDto reviewDto) {
        User reader = userRepository.findById(reviewDto.getReader().getId()).get();
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (blockService.isThereABlock(reader.getId()))
            throw new UserWarningException("Erişim Yok");

        if (reviewDto.getReviewType() == ReviewType.MEETING && !haveUsersMeetRecently(writer, reader))
            throw new UserWarningException("Daha önce bir aktiviteye katılmadınız!");

        if (isReviewedBefore(reviewDto.getReader().getId()))
            throw new UserWarningException("Bir kişi için daha önce referans yazdınız");


        Review review = modelMapper.map(reviewDto, Review.class);
        review.setWriter(writer);
        review.setReader(reader);
        review.setReviewType(ReviewType.FRIEND);

        reviewRepository.save(review);
        userEventService.reviewWritten(reader, review);

    }


    public Boolean isReviewedBefore(Long id) {
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User other = userRepository.findById(id).get();

        Boolean isReviewedBefore = true;
        Review myPreviousReview = reviewRepository.myPreviousReview(me, other);
        if (myPreviousReview == null)
            isReviewedBefore = false;

        return isReviewedBefore;
    }

    public List<ReviewDto> reviewsOfUser(Long id) {
        User user = userRepository.findById(id).get();

        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");


        List<Review> reviews = reviewRepository.findByReader(user);

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);
            reviewDto.setWriter(modelMapper.map(review.getWriter(), ProfileDto.class));
            reviewDto.setReader(modelMapper.map(review.getReader(), ProfileDto.class));
            reviewDtos.add(reviewDto);
        }
        return reviewDtos;
    }


    public ReviewDto findById(Long id) {
        Review review = reviewRepository.findById(id).get();
        ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);
        reviewDto.setWriter(modelMapper.map(review.getWriter(), ProfileDto.class));
        reviewDto.setReader(modelMapper.map(review.getReader(), ProfileDto.class));

        return reviewDto;
    }

    public Boolean haveIMeetThisUserRecently(Long otherUserId) {
        User me  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User other  =userRepository.findById(otherUserId).get();

        return  haveUsersMeetRecently(me,other);
    }
}
