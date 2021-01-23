//package com.alinso.myapp.service;
//
//import com.alinso.myapp.entity.Event;
//import com.alinso.myapp.entity.Hashtag;
//import com.alinso.myapp.entity.User;
//import com.alinso.myapp.entity.dto.event.EventDto;
//import com.alinso.myapp.entity.dto.user.ProfileDto;
//import com.alinso.myapp.repository.HashtagRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//
//@Service
//public class HashtagService {
//
//    @Autowired
//    HashtagRepository hashtagRepository;
//
//    @Autowired
//    ActivityService activityService;
//
//    @Autowired
//    UserService userService;
//
//    public void deleteByActivity(Event event) {
//        hashtagRepository.deleteByActivity(event);
//    }
//
//    public List<Hashtag> findByActivity(Event event) {
//        List<Hashtag> hashtagList = hashtagRepository.findByActivity(event);
//        return hashtagList;
//    }
//
//    public String findByActivityStr(Event event) {
//        List<Hashtag> hashtagList = findByActivity(event);
//        StringBuilder builder = new StringBuilder();
//        for (Hashtag hashtag : hashtagList) {
//            builder.append("#" + hashtag.getName());
//        }
//
//        return builder.toString();
//    }
//
//    public void saveUserHashtag(User user, String hashtagListString) {
//
//        hashtagRepository.deleteByUser(user);
//
//        if (hashtagListString == null)
//            return;
//
//        String[] hashtagList = hashtagListString.trim().split("#");
//        for (String hashtag : hashtagList) {
//            if (hashtag.equals(""))
//                continue;
//            Hashtag hashtagEntity = new Hashtag();
//            hashtagEntity.setName(hashtag);
//            hashtagEntity.setUser(user);
//            hashtagRepository.save(hashtagEntity);
//        }
//
//    }
//
//
//    public void saveActivityHashtag(Event event, String hashtagListString) {
//
//        hashtagRepository.deleteByActivity(event);
//
//        if (hashtagListString == null)
//            return;
//
//        String[] hashtagList = hashtagListString.trim().split("#");
//        for (String hashtag : hashtagList) {
//            if (hashtag.equals(""))
//                continue;
//            Hashtag hashtagEntity = new Hashtag();
//            hashtagEntity.setName(hashtag);
//            hashtagEntity.setActivity(event);
//            hashtagRepository.save(hashtagEntity);
//        }
//    }
//
//    public String findByUserStr(User user) {
//        List<Hashtag> hashtagList = findByUser(user);
//        StringBuilder builder = new StringBuilder();
//        for (Hashtag hashtag : hashtagList) {
//            builder.append("#" + hashtag.getName());
//        }
//        return builder.toString();
//    }
//
//    private List<Hashtag> findByUser(User user) {
//        List<Hashtag> hashtagList = hashtagRepository.findByUser(user);
//        return hashtagList;
//    }
//
//
//    public List<EventDto> findActivitiesByHashtag(String hashtag, Integer pageNum) {
//        String clearHashtag = hashtag.trim().replace("#", "");
//        Pageable pageable  = PageRequest.of(pageNum,5);
//        List<Event> activities = hashtagRepository.findActivitiesByHashtag(clearHashtag,pageable);
//        return activityService.toDtoList(activities);
//    }
//
//    public List<ProfileDto> findUsersByHashtag(String hashtag,Integer pageNum) {
//        Pageable pageable  = PageRequest.of(pageNum,20);
//        String clearHashtag = hashtag.trim().replace("#", "");
//        List<User> users = hashtagRepository.findUsersByHashtag(clearHashtag,pageable);
//        return userService.toProfileDtoList(users);
//
//    }
//}
