package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.repository.EventRequestRepository;
import com.alinso.myapp.repository.FollowRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserEventService {


    private final Integer NEW_EVENT_POINT = 0;
    private final Integer NEW_APPROVAL_POINT = 0;
    private final Integer FRIEND_REVIEW_POINT = 0;
    private final Integer eventrewiew = 0;

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
    EventRequestRepository eventRequestRepository;


    public void newEvent(User user, Event event) {
        //user.setPoint((user.getPoint() + NEW_ACTIVITY_POINT));
        //user.setActivityCount((user.getActivityCount() + 1));

        List<User> followers = new ArrayList<>();

        if(!event.getSecret())
        for(Follow follower:followRepository.findFollowersOfUser(user)){
            if(!blockService.isThereABlock(follower.getFollower().getId()))
           followers.add(follower.getFollower());
        }
        notificationService.newEvent(followers, event.getId());
        userRepository.save(user);
    }


//    public void removeMeeting(Activity activity) {
//        List<User> attendants = activityRequesRepository.attendantsOfActivity(activity, ActivityRequestStatus.APPROVED);
//        for(User user: attendants){
//          //  user.setPoint((user.getPoint() - NEW_APPROVAL_POINT));
//            //user.setActivityCount((user.getActivityCount() - 1));
//            userRepository.save(user);
//        }
//
//        User creator  = activity.getCreator();
//        //this means  that creator user approved someone and get extra 3 points,we should delete them too
//        if(attendants.size()>0){
//          //  creator.setPoint(creator.getPoint()- NEW_APPROVAL_POINT);
//        }
//
//        //creator.setPoint(creator.getPoint()- NEW_ACTIVITY_POINT);
//       // creator.setActivityCount(creator.getActivityCount()-1);
//        userRepository.save(creator);
//    }

    public void newMessage(User reader){
        notificationService.newMessage(reader);
    }

    public void newRequest(User target,Long itemId){
        notificationService.newRequest(target,itemId);
    }

    public void newApproval(User target, Event event) {
        notificationService.newRequestApproval(target, event.getId());

        //give APPROVED user his points
       // target.setPoint((target.getPoint()+ NEW_APPROVAL_POINT));
       // target.setActivityCount((target.getActivityCount()+1));
       // userRepository.save(target);

        //if this is the first approval give creator user his points
      //  List<User> attendants  = activityRequesRepository.attendantsOfActivity(activity,ActivityRequestStatus.APPROVED);
//        if(attendants.size()==1){
//            User creator  =activity.getCreator();
//            creator.setPoint(creator.getPoint()+NEW_APPROVAL_POINT);
//            userRepository.save(creator);
//        }
    }

//    public void cancelApproval(User target, Activity activity){
//        //REMOVE APPROVED user points
////        target.setPoint((target.getPoint()- NEW_APPROVAL_POINT));
////        target.setActivityCount((target.getActivityCount()-1));
////        userRepository.save(target);
//
//        //if this is the first approval REMOVE creator user points
//        List<User> attendants  = activityRequesRepository.attendantsOfActivity(activity,ActivityRequestStatus.APPROVED);
//        if(attendants.size()==0){
//            User creator  =activity.getCreator();
//            creator.setPoint(creator.getPoint()-NEW_APPROVAL_POINT);
//            userRepository.save(creator);
//        }
//    }

    //if the request is approved and request-owner user takes back the request we need to take back the points
//    public void removeApprovedRequestPoints(ActivityRequest activityRequest){
//        if(activityRequest.getActivityRequestStatus()==ActivityRequestStatus.APPROVED){
//            User user = activityRequest.getApplicant();
//            user.setPoint(user.getPoint()-NEW_ACTIVITY_POINT);
//            user.setActivityCount(user.getActivityCount()-1);
//            userRepository.save(user);
//        }
//    }

//    public void reviewWritten(User reader, Review review){
////        Integer point = 0;
////        if (review.getReviewType() == ReviewType.FRIEND)
////            point = FRIEND_REVIEW_POINT;
////        if (review.getReviewType() == ReviewType.MEETING)
////            point = ACTIVITY_REVIEW_POINT;
////
////        if (review.getPositive())
////            reader.setPoint(reader.getPoint() + point);
////        else
////            reader.setPoint(reader.getPoint() - point);
////
////        reader.setReviewCount(reader.getReviewCount() + 1);
////        userRepository.save(reader);
//    //    notificationService.newReview(reader,review.getId());
//
//    }

//    public void newPhotoAdded() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        user.setPhotoCount((user.getPhotoCount() + 1));
//        userRepository.save(user);
//    }
//
//    public void photoDeleted() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        user.setPhotoCount((user.getPhotoCount() - 1));
//        userRepository.save(user);
//    }


//    public void messaesRead() {
//        //TODO: all messages will be readed
//    }


//    public void setReferenceChain(User child){
//
//        //when registering reference code of parent inserted as reference code of this(child) user
//        //we will get that code from db and set a new-real ref-code from child
//        String parentRefCode = child.getReferenceCode();
//        referenceService.createNewReferenceCode(child);
//
//        if (!parentRefCode.equals("") && parentRefCode!=null) {
//            //give points to the referencer
//            User parent = userRepository.findByReferenceCode(parentRefCode).get();
//            parent.setPoint((parent.getPoint() + 5));
//            userRepository.save(parent);
//            //set parent of user
//            child.setParent(parent);
//            userRepository.save(child);
//
//            //check the number of child for this parent
//            List<ProfileDto> children = referenceService.getChildrenOfParent(parent);
//            if(children.size()==5){
//                premiumService.saveGift(parent, PremiumDuration.SONE_MONTH);
//                mailService.newPremiumFor5ReferenceMail(parent);
//            }
//        }
//    }


    public void newUserRegistered(User child) {
    messageService.greetingMessageForNewUser(child);
    notificationService.newGreetingMessage(child);

    //TODO: WİLL BE REMOVED IN FUTURE
        //
        // premiumService.saveGift(child,PremiumDuration.SONE_MONTH);
    }

    public void newMessageEvent(User reader, Event triggerEvent){
        notificationService.newMessageEvent(reader, triggerEvent);
    }

    public void newPremiumMessage(User reader,User writer) {
        notificationService.newPremiumMessage(reader,writer);
    }
}
