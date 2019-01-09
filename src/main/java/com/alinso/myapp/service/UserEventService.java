package com.alinso.myapp.service;

import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserEventService {


    private final Integer NEW_MEETING_POINT = 1;
    private final Integer FRIIEND_REVİEW_POINT = 3;
    private final Integer MEETING_REVİEW_POINT = 5;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    public void newMeeting(User user) {
        user.setPoint((user.getPoint() + NEW_MEETING_POINT));
        user.setMeetingCount((user.getMeetingCount() + 1));
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
            point = FRIIEND_REVİEW_POINT;
        if (review.getReviewType() == ReviewType.MEETING)
            point = MEETING_REVİEW_POINT;

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
}
