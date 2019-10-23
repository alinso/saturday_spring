package com.alinso.myapp.service;


import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.Vibe;
import com.alinso.myapp.entity.dto.vibe.VibeDto;
import com.alinso.myapp.entity.enums.VibeType;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.repository.VibeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VibeService {

    @Autowired
    VibeRepository vibeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityRequesRepository activityRequesRepository;


    public void save(VibeDto vibeDto){

        User writer  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader  =userRepository.findById(vibeDto.getReaderId()).get();

        Vibe vibe  =vibeRepository.findByWriterAndReader(writer,reader);
        if(vibe==null){
            vibe=new Vibe();
        }

        vibe.setReader(reader);
        vibe.setWriter(writer);
        vibe.setDeleted(0);
        vibe.setVibeType(vibeDto.getVibeType());

        vibeRepository.save(vibe);

    }



    public Integer calculateVibe(Long readerId){
        User reader=userRepository.findById(readerId).get();


        List<Vibe> allVibes  = vibeRepository.findByReader(reader);

        if(allVibes.size()<5)
            return 0;

        Integer negativeVibeCount=0;
        Integer haterUserCount=0;

        for(Vibe v:allVibes){

            Boolean isHater=false;

            if(v.getWriter().getTooNegative()!=null) {
                if (v.getWriter().getTooNegative() == 1)
                    haterUserCount++;
            }

            if(v.getWriter().getTooNegative()==1)
                isHater=true;

            if(v.getVibeType()== VibeType.NEGATIVE && !isHater){
                negativeVibeCount++;
            }
        }

        Integer allVibesCount=allVibes.size()-haterUserCount;
        Integer posivitiveVibeCount  =allVibesCount-negativeVibeCount;
        Integer positivePercent= (posivitiveVibeCount*100)/allVibesCount;

        return positivePercent;

    }


    public VibeType myVibeOfThisUser(Long userId) {

        User otherUser = userRepository.findById(userId).get();
        User me  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Vibe vibe = vibeRepository.findByWriterAndReader(me,otherUser);

        if(vibe==null)
            return  null;

        return vibe.getVibeType();
    }

    public Integer calculateVibeOfActivityOwner(Long activityId) {
       Activity activity= activityRepository.findById(activityId).get();
        return calculateVibe(activity.getCreator().getId());
    }

    public Integer vibePercentOfRequestOwner(Long requestId) {
        ActivityRequest activityRequest  =activityRequesRepository.findById(requestId).get();
        return calculateVibe(activityRequest.getApplicant().getId());
    }
}
