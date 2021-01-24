package com.alinso.myapp.service;

import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventRequest;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.review.ReviewDto;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.EventRepository;
import com.alinso.myapp.repository.EventRequestRepository;
import com.alinso.myapp.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventRequestRepository eventRequestRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEventService userEventService;

    @Autowired
    UserService userService;

    @Autowired
    EventRequestService eventRequestService;

    @Autowired
    BlockService blockService;

    @Autowired
    NotificationService notificationService;

    private final Integer DAYS_TO_WRITE_REVIEW = -5;
    private final Integer HOURS_TO_WRITE_REVIEW = -1;


    public Boolean haveUserAttendMeeting(Event event, User user) {
        Boolean result = false;
        EventRequest myRequest = new EventRequest();
        try {
            myRequest = eventRequestRepository.findByEventAndApplicant(user, event);

            //I have attend one of his meetings
            if (myRequest.getEventRequestStatus() == EventRequestStatus.APPROVED) {
                result = true;
            }
        } catch (Exception e) {
            //do nothing
        }
        return result;
    }


//    public Boolean haveUsersMeetRecently(User me, User other) {
//
//
//        Calendar start = Calendar.getInstance();
//        start.setTime(new Date());
//        start.add(Calendar.DATE, DAYS_TO_WRITE_REVIEW);
//
//        Calendar finish = Calendar.getInstance();
//        finish.setTime(new Date());
//        finish.add(Calendar.HOUR, HOURS_TO_WRITE_REVIEW);
//
//
//        List<Event> recentMeetingsCreatedByMe = eventRepository.recentActivitiesOfCreator(start.getTime(), finish.getTime(), me);
//        List<Event> recentMeetingsCreatedByOther = eventRepository.recentActivitiesOfCreator(start.getTime(), finish.getTime(), other);
//
//
//        //have I attend his activity?
//        for (Event event : recentMeetingsCreatedByOther) {
//            if (haveUserAttendMeeting(event, me)) {
//                return true;
//            }
//        }
//
//        //has he attend my activity
//        //if I already attend one of him no need to check this
//
//            for (Event event : recentMeetingsCreatedByMe) {
//                if (haveUserAttendMeeting(event, other)) {
//                    return true;
//                }
//            }
//
//        //has we attend same activity
//        //if they hosted by saame activity
//        List<Event> eventList1 =activityRequesRepository.activitiesAttendedByUser(me, EventRequestStatus.APPROVED);
//        List<Event> eventList2 =activityRequesRepository.activitiesAttendedByUser(other, EventRequestStatus.APPROVED);
//
//
//        for(Event a1 : eventList1){
//            for(Event a2: eventList2){
//                if(a1.getId()==a2.getId()){
//                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
//                    long HOUR_IN_MS = 1000*60*60;
//                    Date twoDaysAgo = new Date(System.currentTimeMillis() - (5 * DAY_IN_MS));
//                    Date oneHourAgo = new Date(System.currentTimeMillis() - HOUR_IN_MS);
//
//                    if(a1.getDeadLine().compareTo(twoDaysAgo)>0  && a1.getDeadLine().compareTo(oneHourAgo)<0){
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//
//    }

    public void writeReview(ReviewDto reviewDto) {
        User reader = userService.findEntityById(reviewDto.getReader().getId());
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Boolean haveUsersMeetRecently = haveUsersMeetRecently(writer, reader);
         Boolean haveUsersMeetRecently = eventRequestService.haveTheseUsersMeetAllTimes(writer.getId(),reader.getId());

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

        reviewRepository.save(review);
        notificationService.newReview(reader,review.getId());

        //    userEventService.reviewWritten(reader, review);

    }

    public void deleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId).get();
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(loggedUser.getId() == review.getReader().getId() || review.getWriter().getId()==loggedUser.getId()){
            reviewRepository.delete(review);
        }
        else{
            throw new UserWarningException("Bunu silme yetkin yok");
        }
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


        List<Review> reviews = reviewRepository.findByReader(user);

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (Review review : reviews) {
            ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);
            reviewDto.setWriter(userService.toProfileDto(review.getWriter()));
            reviewDto.setReader(userService.toProfileDto(review.getReader()));
            reviewDto.setReviewId(review.getId());
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
       // User other  =userService.findEntityById(otherUserId);

        return eventRequestService.haveTheseUsersMeetAllTimes(me.getId(),otherUserId);
        //return  haveUsersMeetRecently(me,other);
    }
}
