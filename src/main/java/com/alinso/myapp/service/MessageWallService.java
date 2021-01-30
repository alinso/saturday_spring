package com.alinso.myapp.service;
import com.alinso.myapp.entity.MessageWall;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.MessageWallDto;
import com.alinso.myapp.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.myapp.repository.MessageWallRepository;
import com.alinso.myapp.util.DateUtil;
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
public class MessageWallService {


    @Autowired
    MessageWallRepository messageWallRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;


    @Autowired
    NotificationService notificationService;

    public void save(String message){

        MessageWall messageWall =  new MessageWall();
        User u= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        messageWall.setWriter(u);
        messageWall.setMessage(message);
        messageWallRepository.save(messageWall);

    }


    public List<MessageWallDto> getAllMessages(){
            Pageable pageable = PageRequest.of(0, 100);
        List<MessageWall> messageWallList = messageWallRepository.findLast200(pageable);
        List<MessageWallDto> messages  = new ArrayList<>();
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        for(int i = (messageWallList.size()-1); i>=0; i--){

            MessageWall g = messageWallList.get(i);

            String profilepicName="upload/"+g.getWriter().getProfilePicName();
            if(profilepicName.equals("upload/"))
                profilepicName="img/user.png";
//
//            String code =  "<div style='float:left'><img class='ghostProfilePic' src='"+profilepicName+"'/></div>" +
//                    "<strong><a style='color:red;font-size:13px' href='/profile/"+g.getWriter().getId()+"'>"+g.getWriter().getName()+" "+g.getWriter().getSurname()+"</a></strong>";
//            String msgConcate = code+"<br/><div style='margin-left:50px;word-wrap:break-word;width:90%;font-size:12px'>"+g.getMessage()+"</div>" +
//                    "<span style='float:right;font-size:11px;color:gray'>"+ DateUtil.dateToString(g.getCreatedAt(),"dd/MM HH:mm")+"</span>";



            String code = loggedUser.getName();



            String msgConcate = "<div style='margin-left:5px;word-wrap:break-word;font-size:12px' >"+g.getMessage()+"<br/><strong>("+code+")</strong></div>" +
                    "<span style='float:right;font-size:11px;color:gray'>"+ DateUtil.dateToString(g.getCreatedAt(),"dd/MM HH:mm")+"</span>";

            MessageWallDto messageWallDto = new MessageWallDto();
            messageWallDto.setMessage(msgConcate);
            messageWallDto.setId(g.getId());
            if(loggedUser.getId()==g.getWriter().getId()){
                messageWallDto.setDelete(1);
            }else{
                messageWallDto.setDelete(0);
            }
            messages.add(messageWallDto);
        }


        //open notification receiving after reading page
        User u =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return messages;
    }

    public List<MessageWallDto> getAllMessagesPC(){
        Pageable pageable = PageRequest.of(0, 100);
        List<MessageWall> messageWallList = messageWallRepository.findLast200(pageable);
        List<MessageWallDto> messages  = new ArrayList<>();
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        for(int i = (messageWallList.size()-1); i>=0; i--){

            MessageWall g = messageWallList.get(i);




            String code = g.getWriter().getName();

            String msgConcate = "<div style='margin-left:5px;word-wrap:break-word;font-size:12px' >"+g.getMessage()+"<br/><strong>("+code+")</strong></div>" +
                    "<span style='float:right;font-size:11px;color:gray'>"+ DateUtil.dateToString(g.getCreatedAt(),"dd/MM HH:mm")+"</span>";

            MessageWallDto messageWallDto = new MessageWallDto();
            messageWallDto.setMessage(msgConcate);
            messageWallDto.setId(g.getId());
            if(loggedUser.getId()==g.getWriter().getId()){
                messageWallDto.setDelete(1);
            }else{
                messageWallDto.setDelete(0);
            }
            messages.add(messageWallDto);
        }


        //open notification receiving after reading page
        User u =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return messages;
    }


    public String uploadPhoto(SinglePhotoUploadDto singlePhotoUploadDto) {
        String extension = FilenameUtils.getExtension(singlePhotoUploadDto.getFile().getOriginalFilename());
        String newName = fileStorageUtil.makeFileName() + "." + extension;

        newName= "wall_"+newName;
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fileStorageUtil.storeFile(singlePhotoUploadDto.getFile(), newName, false);

        //update database

        MessageWall message = new MessageWall();
        message.setMessage("<img src=\"upload/"+newName+"\" width=\"100%\">");
        message.setWriter(loggedUser);
        messageWallRepository.save(message);
        return newName;
    }



    public void deleteMessage(Long messageId){
        MessageWall messageWall = messageWallRepository.findById(messageId).get();
        User loggedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(messageWall.getWriter().getId()==loggedUser.getId()){
            messageWallRepository.delete(messageWall);
        }
    }
}























