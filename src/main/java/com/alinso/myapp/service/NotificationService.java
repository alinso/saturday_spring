package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Notification;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.notification.NotificationDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.NotificationType;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.mail.service.MailService;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.NotificationRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    //Logged user do the thing and notification goes to other user,
    // so when creating notification object logged user is trigger - other user is target

    private final Integer DAYS_TO_WRITE_REVIEW = -10;
    private final Integer HOURS_TO_WRITE_REVIEW = -1;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    MailService  mailService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BlockService blockService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityRequesRepository activityRequesRepository;


    @Autowired
    UserService userService;

    private void createNotification(User target,User trigger,NotificationType notificationType,String message){


        if(blockService.isThereABlock(target.getId()))
            throw new UserWarningException("Erişim Yok");


        Notification notification = new Notification();
        notification.setNotificationType(notificationType);
        notification.setTarget(target);
        notification.setMessage(message);
        notification.setTrigger(trigger);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Scheduled(fixedRate = 60*60*1000, initialDelay = 60*1000)
    public void newMeetingCommentAvailable(){

        //select expired,right-time - non commented meetings
        Calendar start = Calendar.getInstance();
        start.setTime(new Date());
        start.add(Calendar.DATE, DAYS_TO_WRITE_REVIEW);

        Calendar finish = Calendar.getInstance();
        finish.setTime(new Date());
        finish.add(Calendar.HOUR, HOURS_TO_WRITE_REVIEW);

        List<Activity> activityList = activityRepository.recentUncommentedActivities(start.getTime(),finish.getTime());

        for(Activity activity : activityList){
            //check if attendants are more than one
            Long meetingId  = activity.getId();
            if(activityRequesRepository.countOfAprrovedForThisActivity(activity, ActivityRequestStatus.APPROVED)>1){
                //send notification to creator
                createNotification(activity.getCreator(),null,NotificationType.MEETING_COMMENT_AVAILABLE, meetingId.toString());
                mailService.newReviewAvailableMail(activity.getCreator(),meetingId);
                //send notification to attendants
                List<User> attendants  = activityRequesRepository.attendantsOfActivity(activity, ActivityRequestStatus.APPROVED);
                for(User attendant :attendants){
                    createNotification(attendant,null,NotificationType.MEETING_COMMENT_AVAILABLE,meetingId.toString());
                    mailService.newReviewAvailableMail(attendant,meetingId);
                }
            }
            activity.setCommentNotificationSent(true);
            activityRepository.save(activity);
        }
    }

    public void newMessage(User target){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.MESSAGE,null);
        mailService.sendNewMessageMail(target,trigger);
    }

    public void newRequest(User target,Long itemId){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.REQUEST,itemId.toString());
        mailService.sendNewRequestMail(target,trigger,itemId);
    }

    public void newRequestApproval(User target,Long itemId){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.REQUEST_APPROVAL,itemId.toString());
        mailService.sendNewRequestApprovalMail(target,trigger,itemId);
    }

    public void newReview(User target,Long itemId){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.REVIEW,itemId.toString());
        mailService.newReviewMail(target,trigger,itemId);
    }
    public void newMeeting(User target,Long itemId){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.FOLLOWING,itemId.toString());
        mailService.sendNewActivityMail(target,trigger,itemId);
    }
    public void newGeneral(String message,User target){
        createNotification(target,null,NotificationType.GENERAL,message);
    }


    public void read(Long id){

        Notification notification = notificationRepository.findById(id).get();
        UserUtil.checkUserOwner(notification.getTarget().getId());


        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public List<NotificationDto> findLoggedUserNotReadedNotifications(){
        //now the target is logged user because we are reading notifications now, not creating
        User target  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Notification> notifications = notificationRepository.findTargetNotReadedNotifications(target);
        List<NotificationDto> notificationDtos = transformFromEntityToDtoList(notifications);
        return notificationDtos;
    }

    public List<NotificationDto> findLoggedUserAllNotifications(){
        //now the target is logged user because we are reading notifications now, not creating
        User target  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Notification> notifications = notificationRepository.findByTargetOrderByCreatedAtDesc(target);
        List<NotificationDto> notificationDtos  =transformFromEntityToDtoList(notifications);

        return notificationDtos;
    }

    public void readExceptMessages() {
        User target = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Notification> notifications = notificationRepository.findByTarget(target);


        for(Notification notification : notifications){
            if(notification.getNotificationType()!=NotificationType.MESSAGE){
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }


    public List<NotificationDto> transformFromEntityToDtoList(List<Notification> notifications){
        List<NotificationDto> notificationDtos  = new ArrayList<>();
        for(Notification notification  :notifications){
            NotificationDto notificationDto = modelMapper.map(notification, NotificationDto.class);

            ProfileDto targetDto= userService.toProfileDto(notification.getTarget());

            ProfileDto triggerDto = null;
            if(notification.getTrigger()!=null)
                triggerDto  =userService.toProfileDto(notification.getTrigger());

            notificationDto.setTrigger(triggerDto);
            notificationDto.setTarget(targetDto);
            notificationDto.setCreatedAtString(DateUtil.dateToString(notification.getCreatedAt(),"DD/MM/YYYY HH:mm"));
            notificationDtos.add(notificationDto);
        }

        return notificationDtos;
    }


    public void readMessages() {
        User target = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Notification> notifications = notificationRepository.findByTarget(target);


        for(Notification notification : notifications){
            if(notification.getNotificationType()==NotificationType.MESSAGE){
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    public void newGreetingMessage(User target) {
        User trigger  =userService.findEntityById(Long.valueOf(1));
        createNotification(target,trigger,NotificationType.MESSAGE,null);
        mailService.sendNewMessageMail(target,trigger);
    }
}












