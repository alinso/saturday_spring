package com.alinso.myapp;


import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.enums.FollowStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.UserStatus;
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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventFollowTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DayActionRepository dayActionRepository;

    @Autowired
    InterestRepository interestRepository;


    User leader;
    User follower;
    User requester;
    User nonFollower;

    private final String USER_NAME = "KAJSDGFHJADSHKHGADSFKJ";
    private final String USER_PASSWORD = "skdjaklkakskjhjfadlad";
    private final String LEADER_PHONE = "5682235141";
    private final String FOLLOWER_PHONE = "5682235142";
    private final String REQUESTER_PHONE = "5682235143";
    private final String NON_FOLLOWER_PHONE = "5682235144";
    private final String NON_SECRET_EVENT_DETAIL = "public eventtt";
    private final String SECRET_EVENT_DETAIL = "secret eventtt";


    @AfterAll
    private void clearDb() {
        List<Event> events = eventRepository.findByDetail(NON_SECRET_EVENT_DETAIL);
        List<Event> events1 = eventRepository.findByDetail(SECRET_EVENT_DETAIL);
        List<User> users = userRepository.findByName(USER_NAME);

        eventRepository.deleteAll(events);
        eventRepository.deleteAll(events1);
        userRepository.deleteAll(users);
    }

    @BeforeAll
    void populateDb() {

        clearDb();

        City c = cityRepository.findById(Long.valueOf(1)).get();


        User leader = new User();
        leader.setCity(c);
        leader.setInterests(null);
        leader.setName(USER_NAME);
        leader.setSurname("sur");
        leader.setPhone(LEADER_PHONE);
        leader.setGender(Gender.MALE);
        leader.setPassword(USER_PASSWORD);
        leader.setApprovalCode(null);
        leader.setBirthDate(new Date());
        leader.setStatus(UserStatus.REGISTERED);
        leader.setEnabled(true);
        leader.setAbout("about");
        leader.setExtraPercent(0);
        leader.setTooNegative(0);
        leader.setMotivation("motivation");
        userRepository.save(leader);

        User follower = new User();
        follower.setCity(c);
        follower.setInterests(null);
        follower.setName(USER_NAME);
        follower.setSurname("sur");
        follower.setEnabled(true);
        follower.setPhone(FOLLOWER_PHONE);
        follower.setGender(Gender.MALE);
        follower.setPassword(USER_PASSWORD);
        follower.setApprovalCode(null);
        follower.setBirthDate(new Date());
        follower.setStatus(UserStatus.REGISTERED);
        follower.setAbout("about");
        follower.setExtraPercent(0);
        follower.setTooNegative(0);
        follower.setMotivation("motivation");
        userRepository.save(follower);

        User requester = new User();
        requester.setCity(c);
        requester.setInterests(null);
        requester.setName(USER_NAME);
        requester.setSurname("sur");
        requester.setEnabled(true);
        requester.setPhone(REQUESTER_PHONE);
        requester.setGender(Gender.MALE);
        requester.setPassword(USER_PASSWORD);
        requester.setApprovalCode(null);
        requester.setBirthDate(new Date());
        requester.setStatus(UserStatus.REGISTERED);
        requester.setAbout("about");
        requester.setExtraPercent(0);
        requester.setTooNegative(0);
        requester.setMotivation("motivation");
        userRepository.save(requester);

        User nonFollower = new User();
        nonFollower.setCity(c);
        nonFollower.setInterests(null);
        nonFollower.setName(USER_NAME);
        nonFollower.setSurname("sur");
        nonFollower.setPhone(NON_FOLLOWER_PHONE);
        nonFollower.setGender(Gender.MALE);
        nonFollower.setPassword(USER_PASSWORD);
        nonFollower.setApprovalCode(null);
        nonFollower.setBirthDate(new Date());
        nonFollower.setStatus(UserStatus.REGISTERED);
        nonFollower.setAbout("about");
        nonFollower.setEnabled(true);
        nonFollower.setExtraPercent(0);
        nonFollower.setTooNegative(0);
        nonFollower.setMotivation("motivation");
        userRepository.save(nonFollower);

        Follow follow = new Follow();
        follow.setLeader(leader);
        follow.setFollower(follower);
        follow.setStatus(FollowStatus.APPROVED);
        followRepository.save(follow);


        Follow request = new Follow();
        request.setLeader(leader);
        request.setFollower(requester);
        request.setStatus(FollowStatus.WAITING);
        followRepository.save(request);

        this.leader = leader;
        this.follower = follower;
        this.requester = requester;
        this.nonFollower = nonFollower;

    }


    @Test
    @WithMockUser(username = LEADER_PHONE, password = USER_PASSWORD)
    public void createPublicEvent() throws Exception {
        setUserInterests(leader,true);
        dayActionRepository.clearEvent();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/event/create").with(user(leader))
                .param("cityId", "1")
                .param("detail", NON_SECRET_EVENT_DETAIL)
                .param("selectedInterestIds", "3,4")
                .param("deadLineString", "06/11/2022 18:00")
                .param("secret", "false")
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = FOLLOWER_PHONE, password = USER_PASSWORD)
    public void followerSeePublicEvent() throws Exception {
        setUserInterests(follower,true);
        createPublicEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(follower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(NON_SECRET_EVENT_DETAIL)));
    }

    @Test
    @WithMockUser(username = REQUESTER_PHONE, password = USER_PASSWORD)
    public void requesterSeePublicEvent() throws Exception {
        setUserInterests(requester,true);
        createPublicEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(requester))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(NON_SECRET_EVENT_DETAIL)));
    }

    @Test
    @WithMockUser(username = NON_FOLLOWER_PHONE, password = USER_PASSWORD)
    public void nonFollowerSeePublicEvent() throws Exception {
        setUserInterests(nonFollower,true);
        createPublicEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(nonFollower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(NON_SECRET_EVENT_DETAIL)));
    }

    @Test
    @WithMockUser(username = LEADER_PHONE, password = USER_PASSWORD)
    public void createSecretEvent() throws Exception {
        setUserInterests(leader,true);
        dayActionRepository.clearEvent();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/event/create").with(user(leader))
                .param("cityId", "1")
                .param("detail", SECRET_EVENT_DETAIL)
                .param("selectedInterestIds", "3,4")
                .param("deadLineString", "06/11/2022 18:00")
                .param("secret", "true")
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = FOLLOWER_PHONE, password = USER_PASSWORD)
    public void followerSeeSecretEvent() throws Exception {

        setUserInterests(follower,true);
        createSecretEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(follower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SECRET_EVENT_DETAIL)));
    }

    @Test
    @WithMockUser(username = REQUESTER_PHONE, password = USER_PASSWORD)
    public void requesterWontSeeSecretEvent() throws Exception {
        setUserInterests(requester,true);
        createSecretEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(requester))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult mvcResult) throws Exception {
                assertTrue(!mvcResult.getResponse().getContentAsString().contains(SECRET_EVENT_DETAIL));
            }
        });
    }

    @Test
    @WithMockUser(username = NON_FOLLOWER_PHONE, password = USER_PASSWORD)
    public void nonFollowerWontSeeSecretEvent() throws Exception {
        setUserInterests(nonFollower,true);
        createSecretEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(nonFollower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult mvcResult) throws Exception {
                assertTrue(!mvcResult.getResponse().getContentAsString().contains(SECRET_EVENT_DETAIL));
            }
        });
    }

    @Test
    @WithMockUser(username = FOLLOWER_PHONE, password = USER_PASSWORD)
    public void followerWontSeePublicEventNotInterested() throws Exception {

        setUserInterests(follower,false);

        createPublicEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(follower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(NON_SECRET_EVENT_DETAIL));
                    }
                });
    }


    @Test
    @WithMockUser(username = FOLLOWER_PHONE, password = USER_PASSWORD)
    public void followerWontSeeSecretEventNotInterested() throws Exception {

        setUserInterests(follower,false);

        createSecretEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(follower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(SECRET_EVENT_DETAIL));
                    }
                });
    }
    @Test
    @WithMockUser(username = REQUESTER_PHONE, password = USER_PASSWORD)
    public void requesterWontSeePublicEventNotInterested() throws Exception {

        setUserInterests(requester,false);

        createPublicEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(requester))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(NON_SECRET_EVENT_DETAIL));
                    }
                });
    }
    @Test
    @WithMockUser(username = NON_FOLLOWER_PHONE, password = USER_PASSWORD)
    public void nonFollowerWontSeePublicEventNotInterested() throws Exception {

        setUserInterests(nonFollower,false);

        createPublicEvent();

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(nonFollower))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(NON_SECRET_EVENT_DETAIL));
                    }
                });
    }

    private void setUserInterests(User user, boolean all) {
        Set<Interest> interests = new HashSet<>();
        if (all) {
            List<Interest> interestList = interestRepository.findAll();
            for (Interest i : interestList) {
                interests.add(i);
            }
        }else{
            Interest i = interestRepository.findById(Long.valueOf(1)).get();
            Interest i1 = interestRepository.findById(Long.valueOf(2)).get();
            interests.add(i);
            interests.add(i1);
        }
        user.setInterests(interests);
        userRepository.save(user);
    }


}

















