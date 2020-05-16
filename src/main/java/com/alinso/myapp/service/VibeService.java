package com.alinso.myapp.service;


import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.ActivityRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.Vibe;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.vibe.VibeDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.VibeType;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.repository.VibeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    UserService userService;

    @Autowired
    ActivityRequestService activityRequestService;


    public void save(VibeDto vibeDto) {

        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader = userRepository.findById(vibeDto.getReaderId()).get();


        if(activityRequestService.haveTheseUsersMeetAllTimes(writer.getId(),reader.getId())) {

            Vibe vibe = vibeRepository.findByWriterAndReader(writer, reader);
            if (vibe == null) {
                vibe = new Vibe();
            }

            vibe.setReader(reader);
            vibe.setWriter(writer);
            vibe.setDeleted(0);
            vibe.setVibeType(vibeDto.getVibeType());

            vibeRepository.save(vibe);
        }

    }


    public List<ProfileDto> userICanVibe() {

        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        //benim aktiviteme katılanlar
        List<Activity> myActivities = activityRepository.findByCreatorOrderByDeadLineDesc(u);
        Set<User> myActivityAttendants = new HashSet<>();
        for (Activity a : myActivities) {
            List<ActivityRequest> requests = activityRequesRepository.findByActivityId(a.getId());
            for (ActivityRequest r : requests) {

                Integer result = r.getResult();

                if(result!=null)
                if (r.getActivityRequestStatus() == ActivityRequestStatus.APPROVED && result==1) {
                    myActivityAttendants.add(r.getApplicant());
                }
            }
        }


        List<ActivityRequest> activitiesIAttend = activityRequesRepository.findByApplicantId(u.getId());
        for (ActivityRequest r : activitiesIAttend) {


            //only add if I attended it
            Integer result = r.getResult();
            if(result!=null)
            if (r.getActivityRequestStatus() == ActivityRequestStatus.APPROVED && r.getActivity().getCreator().getId() != 3212 &&  result==1) {

                //add other attendants
                List<ActivityRequest> otherApprovedRequests = activityRequesRepository.findByActivityId(r.getActivity().getId());
                for (ActivityRequest otherApprovedRequest : otherApprovedRequests) {

                    if (otherApprovedRequest.getActivityRequestStatus() == ActivityRequestStatus.APPROVED && otherApprovedRequest.getApplicant().getId() != u.getId()) {
                        myActivityAttendants.add(otherApprovedRequest.getApplicant());
                    }
                }
            }

            //add activity owner
            myActivityAttendants.add(r.getActivity().getCreator());
        }


        List<ProfileDto> profilesICanVibe = new ArrayList<>();
        for (User userIcanVibe : myActivityAttendants) {


                VibeType myVibe = myVibeOfThisUser(userIcanVibe.getId());
                ProfileDto profileICanvie  =userService.toProfileDto(userIcanVibe);
                profileICanvie.setMyVibe(myVibe);
                profilesICanVibe.add(profileICanvie);
        }


        Collections.sort(profilesICanVibe, new Comparator<ProfileDto>() {
            @Override
            public int compare(ProfileDto lhs, ProfileDto rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getId() > rhs.getId() ? -1 : (lhs.getId() < rhs.getId()) ? 1 : 0;
            }
        });


        return profilesICanVibe;

    }


    public Integer calculateVibe(Long readerId) {
        User reader = userRepository.findById(readerId).get();


        List<Vibe> allVibes = vibeRepository.findByReaderNonDeleted(reader);

        if (allVibes.size() < 8)
            return 0;

        Integer negativeVibeCount = 0;
        Integer haterUserCount = 0;

        for (Vibe v : allVibes) {


            Boolean isHater = false;

            if (v.getWriter().getTooNegative() != null) {
                if (v.getWriter().getTooNegative() == 1)
                    haterUserCount++;
            }

            if (v.getWriter().getTooNegative() == 1)
                isHater = true;

            if (v.getVibeType() == VibeType.NEGATIVE && !isHater) {
                negativeVibeCount++;
            }
        }

        Integer allVibesCount = allVibes.size() - haterUserCount;
        Integer posivitiveVibeCount = allVibesCount - negativeVibeCount;
        Integer positivePercent = (posivitiveVibeCount * 100) / allVibesCount;

        return positivePercent;
    }


    public VibeType myVibeOfThisUser(Long userId) {

        User otherUser = userRepository.findById(userId).get();
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Vibe vibe = vibeRepository.findByWriterAndReader(me, otherUser);

        if (vibe == null)
            return null;

        return vibe.getVibeType();
    }



    public Integer calculateVibeOfActivityOwner(Long activityId) {
        Activity activity = activityRepository.findById(activityId).get();
        return calculateVibe(activity.getCreator().getId());
    }

    public Integer vibePercentOfRequestOwner(Long requestId) {
        ActivityRequest activityRequest = activityRequesRepository.findById(requestId).get();
        return calculateVibe(activityRequest.getApplicant().getId());
    }

    public Integer vibeCountOfUser(Long userId) {

        User reader=  userRepository.findById(userId).get();
        List<Vibe> vibeCount = vibeRepository.findByReaderNonDeleted(reader);
        return vibeCount.size();
    }

    public void deleteVotesOfNonComingUser(Activity activity, User nonComingUser) {

        List<User> attendants = activityRequestService.findAttendantEntities(activity);

        attendants.add(activity.getCreator());
        for(User u:attendants){
            if(!activityRequestService.haveTheseUsersMeetAllTimes(u.getId(),nonComingUser.getId())){
                Vibe v = vibeRepository.findByWriterAndReader(u,nonComingUser);
                if(v!=null) {
                    v.setDeleted(1);
                    vibeRepository.save(v);
                }
                Vibe v2 = vibeRepository.findByWriterAndReader(nonComingUser,u);
                if(v2!=null) {
                    v2.setDeleted(1);
                    vibeRepository.save(v2);
                }
            }
        }
    }

    public void recoverVibesOfApplicant(User applicant) {

        //votes applicant has given to others
        List<Vibe> deletedVibesOfApplicant = vibeRepository.findByWriterOnlyDeleted(applicant);

        //votes given to the applicant
        List<Vibe> deletedVibesOfApplicant2 = vibeRepository.findByReaderOnlyDeleted(applicant);
        deletedVibesOfApplicant.addAll(deletedVibesOfApplicant2);

        for (Vibe v : deletedVibesOfApplicant) {
            if(activityRequestService.haveTheseUsersMeetAllTimes(v.getWriter().getId(),v.getReader().getId())){
                v.setDeleted(0);
                vibeRepository.save(v);
            }
        }
    }
}
























