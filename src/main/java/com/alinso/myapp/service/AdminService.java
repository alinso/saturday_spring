package com.alinso.myapp.service;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.message.MessageDto;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.*;
import com.alinso.myapp.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    UserService userService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ComplainRepository complainRepository;

    @Autowired
    EventService eventService;


    @Autowired
    MessageService messageService;

    @Autowired
    EventRequestRepository eventRequestRepository;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    PhotoService photoService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ReferenceService referenceService;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    EventRequestService eventRequestService;

    /////////////////////admin////////////////////////////////////////////////////////////////////////////////
    public List<Complain> getAllComplaints() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedUser.getId() != 3212)
            return null;

        return complainRepository.findAll();
    }


    public void resetPassword(Long id) {
        User user = userService.findEntityById(id);
        user.setPassword("$2a$10$vbdDvwd/ZVsD1avjqUVzAOO7JNJm/6kj3xReWEWJfEQ9QnqGYXcO2");
        userRepository.save(user);
    }

    public void deleteByIdAdmin(Long id) {
        try {


            User currentBatman = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (currentBatman.getId() != 3212)
                throw new UserWarningException("Erişim Yok!");


            User user = userRepository.getOne(id);
            List<Event> res = eventRepository.findByCreatorOrderByDeadLineDesc(user);

            for (Event a : res) {
                List<EventRequest> eventRequests = eventRequestRepository.findByEventId(a.getId());
                for (EventRequest r : eventRequests) {
                    eventRequestRepository.deleteById(r.getId());
                }
            }
            //Delete profile photo
            String profilePhoto = user.getProfilePicName();
            fileStorageUtil.deleteFile(profilePhoto);

            //Delete album photos
            List<Photo> photos = photoService.findByUserId(id);
            for (Photo p : photos) {
                photoService.deletePhoto(p.getFileName());
            }

            //Delete EVENT Photos
            List<Event> meetingsCreatedByUser = eventRepository.findByCreatorOrderByDeadLineDesc(user);
            for (Event a : meetingsCreatedByUser) {
                if (a.getPhotoName() != null)
                    fileStorageUtil.deleteFile(a.getPhotoName());
            }

            //move children todo:aliinsan handle references
//            User batman = userRepository.getOne(Long.valueOf(3211));
//            List<User> children = referenceService.getChildrenOfParent(user);
//            for (User child : children) {
//                child.setParent(batman);
//                userRepository.save(child);
//            }

            User u = new User();
            u.setName("silinen");
            u.setSurname("kullanıcı");
            u.setProfilePicName("");
            u.setAbout("");
            u.setPoint(0);
            u.setMotivation("");
            u.setGender(user.getGender());
            u.setPhone(user.getPhone());
            u.setPassword("$2a$10$vbdDvwd/ZdsDdavjdUVzdOd7dNJm/6kk3xRehEWJtEQ9QntGYXcO2");
            userRepository.save(u);

            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
            delete_user_sp.setParameter("userId", id);
            delete_user_sp.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteEvent(Long id) {
        User currentBatman = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentBatman.getId() != 3211)
            throw new UserWarningException("Erişim Yok!");


        Event event = eventRepository.getOne(id);

        MessageDto messageDto = new MessageDto();
        messageDto.setReader(userService.toProfileDto(event.getCreator()));
        messageDto.setMessage("Merhaba:) İceriği açıklayıcı-net olmayan" +
                " veya ticari amaç taşıyan " +
                "veya tanıtım amacı taşıyan " +
                "veya organizatör-katılımcı modelinde " +
                "veya ahlak-mantık-yasa dışı " +
                "veya uygulama amacına uymayan " +
                "veya büyük kalabalıklara hitap eden " +
                "veya sanal bir platformda grup oluşturma amaçlı " +
                "veya biriyle ortak olarak açılmış " +
                "veya aynısından iki tane açılmış " +
                "veya sanal bir platformun tanıtımı amaçlı veya genal reklam-tanıtım amaçlı aktiviteleri siliyoruz. Bunların tekrarı halinde " +
                " bu aktiviteleri açan hesapları siliyoruz. Aktiviten bu kurallarımızdan bir veya birkaçını ihlal ediyor. Buna dikkat ederek  aktivitelerini açarsan seviniriz, teşekkürler");

        messageService.send(messageDto);

    }


    //////////////////batman/////////////////////////////////////////////////////////////////////////////////////
    public void updateInvalidUsername(Long id) {
        User user = userService.findEntityById(id);
        user.setName("GEÇERSİZ");
        user.setSurname("İSİM");
        userRepository.save(user);

        MessageDto messageDto = new MessageDto();
        messageDto.setReader(userService.toProfileDto(user));
        messageDto.setMessage("Merhaba:) EVENT Friend içinde insanların gerçek-tam isimlerini kullanmaları gerektiğini düşünüyoruz. Bu nedenle tam ismini kullanırsan seviniriz:)");

        messageService.send(messageDto);

    }

    public void updateExtraPoint(Long id, Integer extraPoint) {
        User user = userService.findEntityById(id);
        user.setExtraPercent(extraPoint);
        userRepository.save(user);

    }

    public User userInfo(Long id) {
        return userService.findEntityById(id);
    }


    public List<Long> deletePartyVotes() {
        List<Vote> allVotes = voteRepository.findAll();
        List<Long> deletedVoteIds = new ArrayList<>();
        for (Vote v : allVotes) {
            if(!eventRequestService.haveTheseUsersMeetAllTimes(v.getWriter().getId(),v.getReader().getId())){
                deletedVoteIds.add(v.getId());
                v.setDeleted(1);
                voteRepository.save(v);
            }
            else{
                v.setDeleted(0);
                voteRepository.save(v);
            }

        }
        return deletedVoteIds;
    }
}
