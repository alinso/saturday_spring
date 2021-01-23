package com.alinso.myapp.service;


import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.EventRequest;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.Vote;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.dto.vote.VoteDto;
import com.alinso.myapp.entity.enums.EventRequestStatus;
import com.alinso.myapp.entity.enums.VoteType;
import com.alinso.myapp.repository.EventRepository;
import com.alinso.myapp.repository.EventRequestRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoteService {

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventRequestRepository eventRequestRepository;

    @Autowired
    UserService userService;

    @Autowired
    EventRequestService eventRequestService;


    public void save(VoteDto voteDto) {

        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader = userRepository.findById(voteDto.getReaderId()).get();


        if(eventRequestService.haveTheseUsersMeetAllTimes(writer.getId(),reader.getId())) {

            Vote vote = voteRepository.findByWriterAndReader(writer, reader);
            if (vote == null) {
                vote = new Vote();
            }

            vote.setReader(reader);
            vote.setWriter(writer);
            vote.setDeleted(0);
            vote.setVoteType(voteDto.getVoteType());

            voteRepository.save(vote);
        }

    }


    public List<ProfileDto> userICanVote() {

        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        //benim aktiviteme katılanlar
        List<Event> myEvents = eventRepository.findByCreatorOrderByDeadLineDesc(u);
        Set<User> myEventAttendants = new HashSet<>();
        for (Event a : myEvents) {
            List<EventRequest> requests = eventRequestRepository.findByEventId(a.getId());
            for (EventRequest r : requests) {

                Integer result = r.getResult();

                if(result!=null)
                if (r.getEventRequestStatus() == EventRequestStatus.APPROVED && result==1) {
                    myEventAttendants.add(r.getApplicant());
                }
            }
        }


        List<EventRequest> activitiesIAttend = eventRequestRepository.findByApplicantId(u.getId());
        for (EventRequest r : activitiesIAttend) {


            //only add if I attended it
            Integer result = r.getResult();
            if(result!=null)
            if (r.getEventRequestStatus() == EventRequestStatus.APPROVED && r.getEvent().getCreator().getId() != 3212 &&  result==1) {

                //add other attendants
                List<EventRequest> otherApprovedRequests = eventRequestRepository.findByEventId(r.getEvent().getId());
                for (EventRequest otherApprovedRequest : otherApprovedRequests) {

                    if (otherApprovedRequest.getEventRequestStatus() == EventRequestStatus.APPROVED && otherApprovedRequest.getApplicant().getId() != u.getId()) {
                        myEventAttendants.add(otherApprovedRequest.getApplicant());
                    }
                }
            }

            //add activity owner
            myEventAttendants.add(r.getEvent().getCreator());
        }


        List<ProfileDto> profilesICanVote = new ArrayList<>();
        for (User userIcanVote : myEventAttendants) {


                VoteType voteType = myVoteOfThisUser(userIcanVote.getId());
                ProfileDto profileICanvie  =userService.toProfileDto(userIcanVote);
                profileICanvie.setMyVote(voteType);
                profilesICanVote.add(profileICanvie);
        }


        Collections.sort(profilesICanVote, new Comparator<ProfileDto>() {
            @Override
            public int compare(ProfileDto lhs, ProfileDto rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getId() > rhs.getId() ? -1 : (lhs.getId() < rhs.getId()) ? 1 : 0;
            }
        });


        return profilesICanVote;

    }


    public Integer calculateVote(Long readerId) {
        User reader = userRepository.findById(readerId).get();


        List<Vote> allVotes = voteRepository.findByReaderNonDeleted(reader);

        if (allVotes.size() < 8)
            return 0;

        Integer negativeVoteCount = 0;
        Integer haterUserCount = 0;

        for (Vote v : allVotes) {


            Boolean isHater = false;

            if (v.getWriter().getTooNegative() != null) {
                if (v.getWriter().getTooNegative() == 1)
                    haterUserCount++;
            }

            if (v.getWriter().getTooNegative() == 1)
                isHater = true;

            if (v.getVoteType() == VoteType.NEGATIVE && !isHater) {
                negativeVoteCount++;
            }
        }

        Integer allVotesCount = allVotes.size() - haterUserCount;
        Integer posivitiveVoteCount = allVotesCount - negativeVoteCount;
        Integer positivePercent = (posivitiveVoteCount * 100) / allVotesCount;

        return positivePercent;
    }


    public VoteType myVoteOfThisUser(Long userId) {

        User otherUser = userRepository.findById(userId).get();
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Vote vote = voteRepository.findByWriterAndReader(me, otherUser);

        if (vote == null)
            return null;

        return vote.getVoteType();
    }



    public Integer calculateVoteOfOrganiser(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        return calculateVote(event.getCreator().getId());
    }

    public Integer votePercentOfRequester(Long requestId) {
        EventRequest eventRequest = eventRequestRepository.findById(requestId).get();
        return calculateVote(eventRequest.getApplicant().getId());
    }

    public Integer voteCountOfUser(Long userId) {

        User reader=  userRepository.findById(userId).get();
        List<Vote> voteCount = voteRepository.findByReaderNonDeleted(reader);
        return voteCount.size();
    }

    public void deleteVotesOfNonComingUser(Event event, User nonComingUser) {

        List<User> attendants = eventRequestService.findAttendantEntities(event);

        attendants.add(event.getCreator());
        for(User u:attendants){
            if(!eventRequestService.haveTheseUsersMeetAllTimes(u.getId(),nonComingUser.getId())){
                Vote v = voteRepository.findByWriterAndReader(u,nonComingUser);
                if(v!=null) {
                    v.setDeleted(1);
                    voteRepository.save(v);
                }
                Vote v2 = voteRepository.findByWriterAndReader(nonComingUser,u);
                if(v2!=null) {
                    v2.setDeleted(1);
                    voteRepository.save(v2);
                }
            }
        }
    }

    public void recoverVotesOfApplicant(User applicant) {

        //votes applicant has given to others
        List<Vote> deletedVotesOfApplicant = voteRepository.findByWriterOnlyDeleted(applicant);

        //votes given to the applicant
        List<Vote> deletedVotesOfApplicant2 = voteRepository.findByReaderOnlyDeleted(applicant);
        deletedVotesOfApplicant.addAll(deletedVotesOfApplicant2);

        for (Vote v : deletedVotesOfApplicant) {
            if(eventRequestService.haveTheseUsersMeetAllTimes(v.getWriter().getId(),v.getReader().getId())){
                v.setDeleted(0);
                voteRepository.save(v);
            }
        }
    }
}
























