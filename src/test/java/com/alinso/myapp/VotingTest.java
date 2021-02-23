package com.alinso.myapp;


import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.enums.*;
import com.alinso.myapp.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VotingTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;


    @Autowired
    CityRepository cityRepository;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EventRequestRepository eventRequestRepository;


    User sharer;
    User viewer;
    User viewer2;
    Event event;

    private final String userName = "jhgfhgfhguhshsullfhfhj";
    private final String viewerUsername = "viewerr";
    private final String viewer2Username = "viewerr2";
    private final String userPassword = "jhgfhgfhgfhxxxfhj";
    private final String sharerPhone = "7788995522";
    private final String viewerPhone = "7788995522";
    private final String viewer2Phone = "7788995522";
    private final String eventDetail = "aksjgfasdhf khasdfg";


    @AfterAll
    private void clearDb() {
        List<Event> events = eventRepository.findByDetail(eventDetail);
        List<User> users = userRepository.findByName(userName);
        List<User> users1 = userRepository.findByName(viewerUsername);
        List<User> users2 = userRepository.findByName(viewer2Username);

        users.addAll(users1);
        users.addAll(users2);

        for (User u : users) {

            List<Vote> votesGiven = voteRepository.findByWriter(u);
            voteRepository.deleteAll(votesGiven);

            List<Vote> votesTaken = voteRepository.findByReader(u);
            voteRepository.deleteAll(votesTaken);
        }

        if(viewer!=null) {
            List<EventRequest> requests = eventRequestRepository.findByApplicantId(viewer.getId());
            eventRequestRepository.deleteAll(requests);
        }

        eventRepository.deleteAll(events);
        userRepository.deleteAll(users);
    }

    @BeforeAll
    public void populateDb() {
        clearDb();

        City c = cityRepository.findById(Long.valueOf(1)).get();
        Set<Interest> interestSet = new HashSet<>();
        Interest interest = interestRepository.findById(Long.valueOf(3)).get();
        Interest interest1 = interestRepository.findById(Long.valueOf(4)).get();
        interestSet.add(interest);
        interestSet.add(interest1);


        sharer = new User();
        sharer.setCity(c);
        sharer.setInterests(interestSet);
        sharer.setName(userName);
        sharer.setSurname("sur");
        sharer.setPhone(sharerPhone);
        sharer.setGender(Gender.MALE);
        sharer.setPassword(userPassword);
        sharer.setApprovalCode(null);
        sharer.setBirthDate(new Date());
        sharer.setStatus(UserStatus.REGISTERED);
        sharer.setEnabled(true);
        sharer.setAbout("about");
        sharer.setExtraPercent(0);
        sharer.setTooNegative(0);
        sharer.setMotivation("motivation");
        userRepository.save(sharer);


        viewer = new User();
        viewer.setCity(c);
        viewer.setInterests(interestSet);
        viewer.setName(viewerUsername);
        viewer.setSurname("sur");
        viewer.setPhone(viewerPhone);
        viewer.setGender(Gender.MALE);
        viewer.setPassword(userPassword);
        viewer.setApprovalCode(null);
        viewer.setBirthDate(new Date());
        viewer.setStatus(UserStatus.REGISTERED);
        viewer.setEnabled(true);
        viewer.setAbout("about");
        viewer.setExtraPercent(0);
        viewer.setTooNegative(0);
        viewer.setMotivation("motivation");
        userRepository.save(viewer);

        viewer2 =  new User();
        viewer2.setCity(c);
        viewer2.setInterests(interestSet);
        viewer2.setName(viewer2Username);
        viewer2.setSurname("sur");
        viewer2.setPhone(viewer2Phone);
        viewer2.setGender(Gender.MALE);
        viewer2.setPassword(userPassword);
        viewer2.setApprovalCode(null);
        viewer2.setBirthDate(new Date());
        viewer2.setStatus(UserStatus.REGISTERED);
        viewer2.setEnabled(true);
        viewer2.setAbout("about");
        viewer2.setExtraPercent(0);
        viewer2.setTooNegative(0);
        viewer2.setMotivation("motivation");
        userRepository.save(viewer2);



        Calendar aYearLater = Calendar.getInstance();
        aYearLater.setTime(new Date());
        aYearLater.add(Calendar.MONTH, 1);

        event = new Event();
        event.setCity(c);
        event.setCreator(sharer);
        event.setInterests(interestSet);
        event.setSecret(false);
        event.setDeadLine(aYearLater.getTime());
        event.setDetail(eventDetail);
        eventRepository.save(event);

    }

    @Test
    @WithMockUser(username = viewerPhone, password = userPassword)
    public void unmetUsersCannotVote() throws Exception {

        clearDb();
        populateDb();
        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(userName));
                    }
                });

    }


    @Test
    @WithMockUser(username = sharerPhone, password = userPassword)
    public void iCanVoteAcceptedGuest() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);

        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(sharer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(mvcResult.getResponse().getContentAsString().contains(viewerUsername));
                    }
                });
    }
    @Test
    @WithMockUser(username = viewerPhone, password = userPassword)
    public void acceptedGuestsCanVoteEachOther1() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);

        EventRequest request2 = new EventRequest();
        request2.setApplicant(viewer2);
        request2.setEvent(event);
        request2.setEventRequestStatus(EventRequestStatus.APPROVED);
        request2.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(mvcResult.getResponse().getContentAsString().contains(viewer2Username));
                    }
                });
    }
    @Test
    @WithMockUser(username = viewer2Phone, password = userPassword)
    public void acceptedGuestsCanVoteEachOther2() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);

        EventRequest request2 = new EventRequest();
        request2.setApplicant(viewer2);
        request2.setEvent(event);
        request2.setEventRequestStatus(EventRequestStatus.APPROVED);
        request2.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(viewer2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(mvcResult.getResponse().getContentAsString().contains(viewerUsername));
                    }
                });
    }

    @Test
    @WithMockUser(username = viewerPhone, password = userPassword)
    public void iCanVoteMyHost() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);

        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(mvcResult.getResponse().getContentAsString().contains(userName));
                    }
                });
    }

    @Test
    @WithMockUser(username = viewerPhone, password = userPassword)
    public void iCanTVoteMyHostAndOthers_ifIdidntCome() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.DIDNT_CAME);
        eventRequestRepository.save(request);

        EventRequest request2 = new EventRequest();
        request2.setApplicant(viewer2);
        request2.setEvent(event);
        request2.setEventRequestStatus(EventRequestStatus.APPROVED);
        request2.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request2);


        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(userName));
                    }
                });

        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(viewer2Username));
                    }
                });
    }


    @Test
    @WithMockUser(username = sharerPhone, password = userPassword)
    public void iCanTVoteMyGuest_ifHedidntCome() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.DIDNT_CAME);
        eventRequestRepository.save(request);

        mockMvc.perform(MockMvcRequestBuilders.get("/vote/usersICanVote/").with(user(sharer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(viewerUsername));
                    }
                });
    }


    @Test
    @WithMockUser(username = sharerPhone, password = userPassword)
    public void delete_undelete_NonComingUserVotes() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);


        EventRequest request2 = new EventRequest();
        request2.setApplicant(viewer2);
        request2.setEvent(event);
        request2.setEventRequestStatus(EventRequestStatus.APPROVED);
        request2.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request2);

        Vote v = new Vote();
        v.setDeleted(0);
        v.setReader(sharer);
        v.setWriter(viewer);
        v.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v);

        Vote v2 = new Vote();
        v2.setDeleted(0);
        v2.setReader(viewer);
        v2.setWriter(sharer);
        v2.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v2);

        Vote v3 = new Vote();
        v3.setDeleted(0);
        v3.setReader(viewer2);
        v3.setWriter(viewer);
        v3.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v3);


        Vote v4 = new Vote();
        v4.setDeleted(0);
        v4.setReader(viewer);
        v4.setWriter(viewer2);
        v4.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v4);



        mockMvc.perform(MockMvcRequestBuilders.get("/request/requestResult/"+request.getId()+"/DIDNT_CAME").with(user(sharer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Vote vInDb = voteRepository.findById(v.getId()).get();
        Vote v2InDb = voteRepository.findById(v2.getId()).get();
        Vote v3InDb = voteRepository.findById(v3.getId()).get();
        Vote v4InDb = voteRepository.findById(v4.getId()).get();

        assertTrue(vInDb.getDeleted()==1);
        assertTrue(v2InDb.getDeleted()==1);
        assertTrue(v3InDb.getDeleted()==1);
        assertTrue(v4InDb.getDeleted()==1);


        mockMvc.perform(MockMvcRequestBuilders.get("/request/requestResult/"+request.getId()+"/CAME").with(user(sharer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

         vInDb = voteRepository.findById(v.getId()).get();
        v2InDb = voteRepository.findById(v2.getId()).get();
        v3InDb = voteRepository.findById(v3.getId()).get();
        v4InDb = voteRepository.findById(v4.getId()).get();

        assertTrue(vInDb.getDeleted()==0);
        assertTrue(v2InDb.getDeleted()==0);
        assertTrue(v3InDb.getDeleted()==0);
        assertTrue(v4InDb.getDeleted()==0);

    }

    @Test
    @WithMockUser(username = viewerPhone, password = userPassword)
    public void delete_removedRequestVote() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);

        Vote v = new Vote();
        v.setDeleted(0);
        v.setReader(sharer);
        v.setWriter(viewer);
        v.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v);

        Vote v2 = new Vote();
        v2.setDeleted(0);
        v2.setReader(viewer);
        v2.setWriter(sharer);
        v2.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v2);


        Vote vInDb = voteRepository.findById(v.getId()).get();
        Vote v2InDb = voteRepository.findById(v2.getId()).get();

        assertTrue(vInDb.getDeleted()==0);
        assertTrue(v2InDb.getDeleted()==0);


        mockMvc.perform(MockMvcRequestBuilders.get("/request/sendRequest/"+event.getId()).with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

         vInDb = voteRepository.findById(v.getId()).get();
         v2InDb = voteRepository.findById(v2.getId()).get();

        assertTrue(vInDb.getDeleted()==1);
        assertTrue(v2InDb.getDeleted()==1);

    }



    @Test
    @WithMockUser(username = sharerPhone, password = userPassword)
    public void delete_unApprovedRequestVote() throws Exception {
        clearDb();
        populateDb();

        EventRequest request = new EventRequest();
        request.setApplicant(viewer);
        request.setEvent(event);
        request.setEventRequestStatus(EventRequestStatus.APPROVED);
        request.setResult(EventRequestResult.CAME);
        eventRequestRepository.save(request);

        Vote v = new Vote();
        v.setDeleted(0);
        v.setReader(sharer);
        v.setWriter(viewer);
        v.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v);

        Vote v2 = new Vote();
        v2.setDeleted(0);
        v2.setReader(viewer);
        v2.setWriter(sharer);
        v2.setVoteType(VoteType.POSITIVE);
        voteRepository.save(v2);


        Vote vInDb = voteRepository.findById(v.getId()).get();
        Vote v2InDb = voteRepository.findById(v2.getId()).get();

        assertTrue(vInDb.getDeleted()==0);
        assertTrue(v2InDb.getDeleted()==0);


        mockMvc.perform(MockMvcRequestBuilders.get("/request/approveRequest/"+request.getId()).with(user(sharer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        vInDb = voteRepository.findById(v.getId()).get();
        v2InDb = voteRepository.findById(v2.getId()).get();

        assertTrue(vInDb.getDeleted()==1);
        assertTrue(v2InDb.getDeleted()==1);

    }




}
