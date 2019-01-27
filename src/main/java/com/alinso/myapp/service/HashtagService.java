package com.alinso.myapp.service;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Hashtag;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.HashtagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HashtagService {

    @Autowired
    HashtagRepository hashtagRepository;


    public void deleteByActivity(Activity activity) {
        hashtagRepository.deleteByActivity(activity);
    }

    public List<Hashtag> findByActivity(Activity activity) {
        List<Hashtag> hashtagList  = hashtagRepository.findByActivity(activity);
        return  hashtagList;
    }

    public String findByActivityStr(Activity activity){
        List<Hashtag> hashtagList = findByActivity(activity);
        StringBuilder builder =   new StringBuilder();
        for(Hashtag hashtag:hashtagList){
            builder.append("#"+hashtag.getName());
        }

        return builder.toString();
    }

    public void saveUserHashtag(User user, String hashtagListString) {

        hashtagRepository.deleteByUser(user);

        if(hashtagListString==null)
            return;

        String[] hashtagList = hashtagListString.trim().split("#");
        for (String hashtag : hashtagList) {
            if(hashtag.equals(""))
                continue;
            Hashtag hashtagEntity = new Hashtag();
            hashtagEntity.setName(hashtag);
            hashtagEntity.setUser(user);
            hashtagRepository.save(hashtagEntity);
        }

    }


    public void saveActivityHashtag(Activity activity, String hashtagListString) {

        hashtagRepository.deleteByActivity(activity);

        if(hashtagListString==null)
            return;

        String[] hashtagList = hashtagListString.trim().split("#");
        for (String hashtag : hashtagList) {
            if(hashtag.equals(""))
                continue;
            Hashtag hashtagEntity = new Hashtag();
            hashtagEntity.setName(hashtag);
            hashtagEntity.setActivity(activity);
            hashtagRepository.save(hashtagEntity);
        }
    }

    public String findByUserStr(User user) {
        List<Hashtag> hashtagList = findByUser(user);
        StringBuilder builder =   new StringBuilder();
        for(Hashtag hashtag:hashtagList){
            builder.append("#"+hashtag.getName());
        }
        return builder.toString();
    }

    private List<Hashtag> findByUser(User user) {
        List<Hashtag> hashtagList  = hashtagRepository.findByUser(user);
        return  hashtagList;
    }
}
