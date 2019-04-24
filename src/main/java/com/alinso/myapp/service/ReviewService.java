package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.review.ReviewDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.ReviewRepository;
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
    UserService userService;

    @Autowired
    BlockService blockService;

    private final Integer DAYS_TO_WRITE_REVIEW = -2;
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


        //have I attend his activity?
        for (Activity activity : recentMeetingsCreatedByOther) {
            if (haveUserAttendMeeting(activity, me)) {
                return true;
            }
        }

        //has he attend my activity
        //if I already attend one of him no need to check this

            for (Activity activity : recentMeetingsCreatedByMe) {
                if (haveUserAttendMeeting(activity, other)) {
                    return true;
                }
            }

        //has we attend same activity
        //if they hosted by saame activity
        List<Activity> activityList1  =activityRequesRepository.activitiesAttendedByUser(me,ActivityRequestStatus.APPROVED);
        List<Activity> activityList2  =activityRequesRepository.activitiesAttendedByUser(other,ActivityRequestStatus.APPROVED);


        for(Activity a1 : activityList1){
            for(Activity a2:activityList2){
                if(a1.getId()==a2.getId()){
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    Date twoDaysAgo = new Date(System.currentTimeMillis() - (2 * DAY_IN_MS));
                    if(a1.getDeadLine().compareTo(twoDaysAgo)>0){
                        return true;
                    }
                }
            }
        }
        return false;

    }

    public void writeReview(ReviewDto reviewDto) {
        User reader = userService.findEntityById(reviewDto.getReader().getId());
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean haveUsersMeetRecently = haveUsersMeetRecently(writer, reader);

//        if (blockService.isThereABlock(reader.getId()))
//            throw new UserWarningException("Erişim Yok");

        if(!haveUsersMeetRecently){
            throw  new UserWarningException("Bu kişi ile son 2 gün içinde onaylanmış bir aktiviten yok, yalnız ortak aktiviten olan kişilere yorum yazabilirsin.\n" +
                    "Sosyalleşmek için bir aktivite oluşturabilir veya birine katılabilirsin :) ");
        }

        if (isReviewedBefore(reviewDto.getReader().getId()))
            throw new UserWarningException("Bir kişi için daha önce yorum yazdın");


        Review review = modelMapper.map(reviewDto, Review.class);
        review.setWriter(writer);
        review.setReader(reader);
        if(haveUsersMeetRecently)
            review.setReviewType(ReviewType.MEETING);


        reviewRepository.save(review);
        //userEventService.reviewWritten(reader, review);

    }


    public Boolean isReviewedBefore(Long id) {
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User other = userService.findEntityById(id);

        Boolean isReviewedBefore = true;
        Review myPreviousReview = reviewRepository.myPreviousReview(me, other);
        if (myPreviousReview == null)
            isReviewedBefore = false;

        return isReviewedBefore;
    }

    public List<ReviewDto> reviewsOfUser(Long id) {
        User user = userService.findEntityById(id);

        if (blockService.isThereABlock(id))
            throw new UserWarningException("Erişim Yok");


        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.setTime(new Date());
        twoDaysAgo.add(Calendar.DATE, DAYS_TO_WRITE_REVIEW);

        List<Review> reviews = reviewRepository.findByReaderBefore2Days(user,twoDaysAgo.getTime());

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);
            reviewDto.setWriter(userService.toProfileDto(review.getWriter()));
            reviewDto.setReader(userService.toProfileDto(review.getReader()));
            reviewDtos.add(reviewDto);
        }
        return reviewDtos;
    }


    public ReviewDto findById(Long id) {
        Review review = reviewRepository.findById(id).get();
        ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);
        reviewDto.setWriter(userService.toProfileDto(review.getWriter()));
        reviewDto.setReader(userService.toProfileDto(review.getReader()));

        return reviewDto;
    }

    public Boolean haveIMeetThisUserRecently(Long otherUserId) {
        User me  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User other  =userService.findEntityById(otherUserId);

        return  haveUsersMeetRecently(me,other);
    }
}
