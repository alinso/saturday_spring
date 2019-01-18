package com.alinso.myapp.service;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Review;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.ReviewType;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.FollowRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEventService {


    private final Integer NEW_MEETING_POINT = 1;
    private final Integer NEW_APPROVAL_POINT = 3;
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

    @Autowired
    ReferenceService referenceService;

    @Autowired
    ActivityRequesRepository activityRequesRepository;


    public void newMeeting(User user, Activity activity) {
        user.setPoint((user.getPoint() + NEW_MEETING_POINT));
        user.setActivityCount((user.getActivityCount() + 1));
        for(User follower:followRepository.findFollowersOfUser(user)){
            if(!blockService.isThereABlock(follower.getId()))
            notificationService.newMeeting(follower, activity.getId());
        }
        userRepository.save(user);
    }


    public void removeMeeting(Activity activity) {
        List<User> attendants = activityRequesRepository.attendantsOfActivity(activity, ActivityRequestStatus.APPROVED);
        for(User user: attendants){
            user.setPoint((user.getPoint() - NEW_APPROVAL_POINT));
            user.setActivityCount((user.getActivityCount() - 1));
            userRepository.save(user);
        }
    }

    public void newMessage(User reader){
        notificationService.newMessage(reader);
    }

    public void newRequest(User target,Long itemId){
        notificationService.newRequest(target,itemId);
    }

    public void newApproval(User target,Activity activity) {
        notificationService.newRequestApproval(target,activity.getId());

        //give APPROVED user his points
        target.setPoint((target.getPoint()+ NEW_APPROVAL_POINT));
        target.setActivityCount((target.getActivityCount()+1));
        userRepository.save(target);

        //if this is the first approval give creator user his points
        List<User> attendants  = activityRequesRepository.attendantsOfActivity(activity,ActivityRequestStatus.APPROVED);
        if(attendants.size()==2){
            User creator  =activity.getCreator();
            creator.setPoint(creator.getPoint()+NEW_APPROVAL_POINT);
            userRepository.save(creator);
        }
    }

    public void cancelApproval(User target, Activity activity){
        //REMOVE APPROVED user points
        target.setPoint((target.getPoint()- NEW_APPROVAL_POINT));
        target.setActivityCount((target.getActivityCount()-1));
        userRepository.save(target);

        //if this is the first approval REMOVE creator user points
        List<User> attendants  = activityRequesRepository.attendantsOfActivity(activity,ActivityRequestStatus.APPROVED);
        if(attendants.size()==1){
            User creator  =activity.getCreator();
            creator.setPoint(creator.getPoint()-NEW_APPROVAL_POINT);
            userRepository.save(creator);
        }
    }

    public void reviewWritten(User reader, Review review){
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
    notificationService.newGreetingMessage(user);

    //give points to the referencer
        referenceService.createNewReferenceCodes(user);
        User parent= referenceService.useReferenceCodeAndReturnParent(user);
        parent.setPoint((parent.getPoint()+5));
        userRepository.save(parent);
    }
}