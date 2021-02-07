package com.alinso.myapp;


import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Event;
import com.alinso.myapp.entity.Interest;
import com.alinso.myapp.entity.User;
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

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserStatusTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CityRepository cityRepository;


    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    DayActionRepository dayActionRepository;

    User sharer;
    User viewer;

    private final String userName = "jhgfhgfhgfhfhj";
    private final String userPassword = "jhgfhgfhgfhxxxfhj";
    private final String sharerPhone = "7788995522";
    private final String viewerPhone = "7788995522";
    private final String eventDetail = "aksjgfasdhf khasdfg";


    @AfterAll
    private void clearDb() {
        List<Event> events = eventRepository.findByDetail(eventDetail);
        List<User> users = userRepository.findByName(userName);

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
        sharer.setStatus(UserStatus.REGISTERED);//thiswill change in test cases
        sharer.setEnabled(true);
        sharer.setAbout("about");
        sharer.setExtraPercent(0);
        sharer.setTooNegative(0);
        sharer.setMotivation("motivation");
        userRepository.save(sharer);


        viewer = new User();
        viewer.setCity(c);
        viewer.setInterests(interestSet);
        viewer.setName(userName);
        viewer.setSurname("sur");
        viewer.setPhone(sharerPhone);
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



        Calendar aYearLater = Calendar.getInstance();
        aYearLater.setTime(new Date());
        aYearLater.add(Calendar.MONTH, 1);

        Event event = new Event();
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
    public void viewerWontSeeTheEventofDisabledUser() throws Exception {

        sharer.setEnabled(false);
        userRepository.save(sharer);

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        assertTrue(!mvcResult.getResponse().getContentAsString().contains(eventDetail));
                    }
                });

    }

    @Test
    @WithMockUser(username = viewerPhone, password = userPassword)
    public void viewerSeeTheEventofEnabledUser() throws Exception {

        sharer.setEnabled(true);
        userRepository.save(sharer);

        mockMvc.perform(MockMvcRequestBuilders.get("/event/findByInterestByCityId/1/0").with(user(viewer))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(eventDetail)));

    }



}
