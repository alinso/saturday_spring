package com.alinso.myapp.service;

import com.alinso.myapp.entity.Block;
import com.alinso.myapp.entity.Meeting;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.repository.FollowRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserEventService {


    private final Integer NEW_MEETING_POINT = 1;
    private final Integer FRIEND_REVIEW_POINT = 3;
    private final Integer MEETING_REVIEW_POINT = 5;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    BlockService blockService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    MessageService messageService;

    public void newMeeting(User user, Meeting meeting) {
        user.setPoint((user.getPoint() + NEW_MEETING_POINT));
        user.setMeetingCount((user.getMeetingCount() + 1));
        for(User follower:followRepository.findFollowersOfUser(user)){
            if(!blockService.isThereABlock(follower.getId()))
            notificationService.newMeeting(follower,meeting.getId());
        }
        userRepository.save(user);
    }


    public void removeMeeting(User user) {
        user.setPoint((user.getPoint() - NEW_MEETING_POINT));
        user.setMeetingCount((user.getMeetingCount() - 1));
        userRepository.save(user);
    }

    public void newMessage(User reader){
        notificationService.newMessage(reader);
    }

    public void newRequest(User target,Long itemId){
        notificationService.newRequest(target,itemId);
    }

    public void newApproval(User target,Long itemId) {
        notificationService.newRequestApproval(target,itemId);
    }

    public void referenceWritten(User reader, Review review){
        Integer point = 0;
        if (review.getReviewType() == ReviewType.FRIEND)
            point = FRIEND_REVIEW_POINT;
        if (review.getReviewType() == ReviewType.MEETING)
            point = MEETING_REVIEW_POINT;

        if (review.getPositive())
            reader.setPoint(reader.getPoint() + point);
        else
            reader.setPoint(reader.getPoint() - point);

        reader.setReviewCount(reader.getReviewCount() + 1);
        userRepository.save(reader);
        notificationService.newReview(reader,review.getId());

    }

    public void newPhotoAdded() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPhotoCount((user.getPhotoCount() + 1));
        userRepository.save(user);
    }

    public void photoDeleted() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPhotoCount((user.getPhotoCount() - 1));
        userRepository.save(user);
    }


    public void messaesRead() {
        //TODO: all messages will be readed
    }

    public void newUserRegistered(User user) {
    messageService.greetingMessageForNewUser(user);
    notificationService.newMessage(user);
    }
}
