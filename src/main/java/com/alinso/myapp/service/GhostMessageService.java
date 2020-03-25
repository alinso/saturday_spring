package com.alinso.myapp.service;
import com.alinso.myapp.entity.GhostMessage;
import com.alinso.myapp.entity.GhostNotification;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.repository.GhostMessageRepository;
import com.alinso.myapp.repository.GhostNotificationRepository;
import com.alinso.myapp.util.FileStorageUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GhostMessageService {


    @Autowired
    GhostMessageRepository ghostMessageRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    GhostNotificationRepository ghostNotificationRepository;

    @Autowired
    NotificationService notificationService;

    public void save(String message){

        GhostMessage ghostMessage =  new GhostMessage();
        User u= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ghostMessage.setWriter(u);
        ghostMessage.setMessage(message);
        ghostMessageRepository.save(ghostMessage);

        List<GhostNotification> notificationList  =ghostNotificationRepository.notificationList(true);

        for(GhostNotification g : notificationList){
            notificationService.newGhostMessage(g.getReceiver());
            g.setSend(false);

            //dont send notification for every message unless user reads the messages and closed the app again
            ghostNotificationRepository.save(g);
        }

    }


    public List<String> getAllMessages(){
        Pageable pageable = PageRequest.of(0, 200);
        List<GhostMessage> ghostMessageList  =ghostMessageRepository.findLast200(pageable);
        List<String> messages  = new ArrayList<>();


        for(int i=(ghostMessageList.size()-1);i>=0;i--){

            GhostMessage g = ghostMessageList.get(i);

            String profilepicName="upload/"+g.getWriter().getProfilePicName();
            if(profilepicName.equals("upload/"))
                profilepicName="img/user.png";

            String code =  "<div style='float:left'><img class='ghostProfilePic' src='"+profilepicName+"'/></div>" +
                    "<strong><a style='color:red' href='/profile/"+g.getWriter().getId()+"'>"+g.getWriter().getName()+" "+g.getWriter().getSurname()+"</a></strong>";
            String msgConcate = code+"<br/><div style='margin-left:50px'>"+g.getMessage()+"</div>";
            messages.add(msgConcate);
        }


        //open notification receiving after reading page
        User u =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        GhostNotification g = ghostNotificationRepository.findByReceiver(u);
        if(g!=null){
            g.setSend(true);
            ghostNotificationRepository.save(g);
        }

        return messages;
    }


    public String uploadPhoto(SinglePhotoUploadDto singlePhotoUploadDto) {
        String extension = FilenameUtils.getExtension(singlePhotoUploadDto.getFile().getOriginalFilename());
        String newName = fileStorageUtil.makeFileName() + "." + extension;

        newName= "ghost_"+newName;
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fileStorageUtil.storeFile(singlePhotoUploadDto.getFile(), newName, false);

        //update database

        GhostMessage message = new GhostMessage();
        message.setMessage("<img src=\"upload/"+newName+"\" width=\"100%\">");
        message.setWriter(loggedUser);
        ghostMessageRepository.save(message);
        return newName;
    }

    public void toggleNotification() {

        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GhostNotification gInDb = ghostNotificationRepository.findByReceiver(u);

        if(gInDb==null) {
            GhostNotification g =  new GhostNotification();
            g.setReceiver(u);
            g.setSend(true);
            ghostNotificationRepository.save(g);
        }else{
            ghostNotificationRepository.delete(gInDb);
        }
    }

    public Boolean isReceivingNotification() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GhostNotification gInDb = ghostNotificationRepository.findByReceiver(u);

        if(gInDb==null)
            return false;
        else
            return true;
    }
}
