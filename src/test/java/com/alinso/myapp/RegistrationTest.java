package com.alinso.myapp;

import com.alinso.myapp.entity.Application;
import com.alinso.myapp.entity.Reference;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ApplicationStatus;
import com.alinso.myapp.repository.ApplicationRepository;
import com.alinso.myapp.repository.ReferenceRepository;
import com.alinso.myapp.repository.UserRepository;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.print.attribute.standard.MediaSize;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegistrationTest {

// USER ID :1 AND USER ID 4 IS USED FOR TESTING.
// THESE 2 USERS SHOULD NOT BE BOTHERED
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ReferenceRepository referenceRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    private final String NAME = "thetestuserformock";
    private final String PHONE = "1234567899";
    private final String REFERENCE_CODE = "12345";
    private final Long APPROVED_USER_ID = Long.valueOf(4);
    private final String APPROVAL_CODE="4scop0rhg3c";
    private final String USED_PHONE="1234567890";


    @Test
    public  void prepareDatabase() {
        List<User> users = userRepository.findByName(NAME);
        userRepository.deleteAll(users);

        Reference reference = referenceRepository.findByCode(REFERENCE_CODE);
        reference.setChild(null);
        referenceRepository.save(reference);

        List<Application> applications = applicationRepository.findByPhoneAndName(PHONE, NAME);
        applicationRepository.deleteAll(applications);

        User approvedUser = userRepository.findById(APPROVED_USER_ID).get();
        //rolback user after register
        approvedUser.setApprovalCode(APPROVAL_CODE);
        approvedUser.setPassword(null);
        approvedUser.setGender(null);
        approvedUser.setEnabled(false);
        approvedUser.setCity(null);
        userRepository.save(approvedUser);

        //delete created reference after  register
        List<Reference> references = referenceRepository.findByParent(approvedUser);
        referenceRepository.deleteAll(references);


    }

    private Map<String, String> createApplication() {
        Map<String, String> map = new HashMap<>();
        map.put("name", NAME);
        map.put("surname", "test");
        map.put("phone", PHONE);
        map.put("referenceCode", REFERENCE_CODE);
        map.put("about", "its all abut meen");
        return map;
    }

    private Map<String, String> createRegisterData() {
        Map<String, String> map = new HashMap<>();
        map.put("password", "123456");
        map.put("confirmPassword", "123456");
        map.put("gender", "1");
        map.put("approvalCode", "4scop0rhg3c");
        return map;
    }

    @Test
    public void emptyName() throws Exception {

        Map<String, String> application = createApplication();
        application.put("name", "");
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name can't be empty")));
    }

    @Test
    public void emptyAbout() throws Exception {

        Map<String, String> application = createApplication();
        application.put("about", "");
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("About can't be empty")));
    }

    @Test
    public void emptySurname() throws Exception {

        Map<String, String> application = createApplication();
        application.put("surname", "");
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Surname can't be empty")));
    }

    @Test
    public void emptyPhone() throws Exception {

        Map<String, String> application = createApplication();
        application.put("phone", "");
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Phone number has to be 10 digits")));
    }

    @Test
    public void usedPhone() throws Exception {

        Map<String, String> application = createApplication();
        application.put("phone", USED_PHONE);
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("This phone number is used")));
    }

    @Test
    public void longPhone() throws Exception {

        Map<String, String> application = createApplication();
        application.put("phone", "123456789012");
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Phone number has to be 10 digits")));
    }

    @Test
    public void wrongRefCode() throws Exception {

        Map<String, String> application = createApplication();
        application.put("referenceCode", "123");//it has to be 6 digits
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Reference Code")));
    }

    @Test
    public void usedRefCode() throws Exception {

        User child = userRepository.findById(Long.valueOf(4)).get();
        Reference reference = referenceRepository.getValidReference(REFERENCE_CODE);
        reference.setChild(child);
        referenceRepository.save(reference);

        Map<String, String> application = createApplication();
        application.put("referenceCode", REFERENCE_CODE);//it has to be 6 digits
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Reference Code")));


        reference.setChild(null);
        referenceRepository.save(reference);
    }

    @Test
    public void saveWithReference() throws Exception {
        prepareDatabase();
        Map<String, String> application = createApplication();
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application savedApplication = applicationRepository.findByPhoneAndName(application.get("phone"), application.get("name")).get(0);

        assertTrue(savedApplication.getSurname().equals(application.get("surname")));
        assertTrue(savedApplication.getAbout().equals(application.get("about")));

        //clear application
        applicationRepository.delete(savedApplication);
    }

    @Test
    public void saveWithoutReference() throws Exception {

        prepareDatabase();
        Map<String, String> application = createApplication();
        application.put("referenceCode", "");
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application savedApplication = applicationRepository.findByPhoneAndName(application.get("phone"), application.get("name")).get(0);

        assertTrue(savedApplication.getSurname().equals(application.get("surname")));
        assertTrue(savedApplication.getAbout().equals(application.get("about")));

        //clear application
        applicationRepository.delete(savedApplication);
    }

    @Test
    public void approveWithReference() throws Exception {

        Map<String, String> application = createApplication();
        JSONObject jsonObject = new JSONObject(application);

        //create an application with ref code
        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application savedApplication = applicationRepository.findByPhoneAndName(application.get("phone"), application.get("name")).get(0);

        //approve that application
        mockMvc.perform(MockMvcRequestBuilders.get("/application/action/approve/" + savedApplication.getId())
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application applicationApproved = applicationRepository.findById(savedApplication.getId()).get();
        assertTrue(applicationApproved.getApplicationStatus() == ApplicationStatus.APPROVED);

        User user = userRepository.findByPhone(application.get("phone"));
        assertTrue(user.getAbout().equals(application.get("about")));
        assertTrue(user.getPhone().equals(application.get("phone")));
        assertTrue(user.getName().equals(application.get("name")));
        assertTrue(user.getSurname().equals(application.get("surname")));

        Reference reference = referenceRepository.findByChild(user); //ever user has one single parent
        assertTrue(reference != null);


        //clear child from reference
        reference.setChild(null);
        referenceRepository.save(reference);
        //clear application
        applicationRepository.delete(savedApplication);
        //clear user
        userRepository.delete(user);
    }

    @Test
    public void approveWithoutReference() throws Exception {

        prepareDatabase();
        Map<String, String> application = createApplication();
        application.put("referenceCode", "");
        JSONObject jsonObject = new JSONObject(application);

        //create an application with ref code
        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application savedApplication = applicationRepository.findByPhoneAndName(application.get("phone"), application.get("name")).get(0);

        //approve that application
        mockMvc.perform(MockMvcRequestBuilders.get("/application/action/approve/" + savedApplication.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Application applicationApproved = applicationRepository.findById(savedApplication.getId()).get();
        assertTrue(applicationApproved.getApplicationStatus() == ApplicationStatus.APPROVED);

        User user = userRepository.findByPhone(application.get("phone"));
        assertTrue(user.getAbout().equals(application.get("about")));
        assertTrue(user.getPhone().equals(application.get("phone")));
        assertTrue(user.getName().equals(application.get("name")));
        assertTrue(user.getSurname().equals(application.get("surname")));

        Reference reference = referenceRepository.findByChild(user); //ever user has one single parent
        assertTrue(reference == null);

        //clear application
        applicationRepository.delete(savedApplication);
        //clear user
        userRepository.delete(user);
    }

    @Test
    public void multiApplicationsWithSameRefCode() throws Exception { //what about 2 applications with same tel no?
        /*
         * There can be multiple applications with same ref code, only the first approval should be
         * valid and other applications with same ref code should lost their ref codes
         *
         * */

        prepareDatabase();
        //First create 2 applications with same ref code
        Map<String, String> application1 = createApplication();
        Map<String, String> application2 = createApplication();

        application2.put("phone", "9876543217");


        //create an application1 with ref code
        JSONObject jsonObject1 = new JSONObject(application1);
        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject1.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Application savedApplication1 = applicationRepository.findByPhoneAndName(application1.get("phone"), application1.get("name")).get(0);


        //create an application2 with ref code
        JSONObject jsonObject2 = new JSONObject(application2);
        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject2.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        //application1 should be saved
        mockMvc.perform(MockMvcRequestBuilders.get("/application/action/approve/" + savedApplication1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        Application applicationApproved = applicationRepository.findById(savedApplication1.getId()).get();
        assertTrue(applicationApproved.getApplicationStatus() == ApplicationStatus.APPROVED);
        User user = userRepository.findByPhone(application1.get("phone"));
        assertTrue(user.getAbout().equals(application1.get("about")));
        assertTrue(user.getPhone().equals(application1.get("phone")));
        assertTrue(user.getName().equals(application1.get("name")));
        assertTrue(user.getSurname().equals(application1.get("surname")));


        //application2 should lost its ref code
        Application savedApplication2last = applicationRepository.findByPhoneAndName(application2.get("phone"), application2.get("name")).get(0);
        assertTrue(savedApplication2last.getReferenceCode().equals(""));


        Reference reference = referenceRepository.findByChild(user);
        //clear child from reference
        reference.setChild(null);
        referenceRepository.save(reference);
        //clear application
        applicationRepository.delete(savedApplication1);
        applicationRepository.delete(savedApplication2last);
        //clear user
        userRepository.delete(user);
    }

    @Test
    public void declineApplication() throws Exception {

        prepareDatabase();
        Map<String, String> application = createApplication();
        JSONObject jsonObject = new JSONObject(application);

        //create an application with ref code
        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application savedApplication = applicationRepository.findByPhoneAndName(application.get("phone"), application.get("name")).get(0);

        //decline that application
        mockMvc.perform(MockMvcRequestBuilders.get("/application/action/decline/" + savedApplication.getId())
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Application applicationDeclined = applicationRepository.findById(savedApplication.getId()).get();
        assertTrue(applicationDeclined.getApplicationStatus() == ApplicationStatus.DECLINED);

        applicationRepository.delete(savedApplication);
    }

    @Test
    public void registerWithoutPassword() throws Exception {

        Map<String, String> registerDto = createRegisterData();
        registerDto.put("password", "");
        JSONObject jsonObject = new JSONObject(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at 6 characters")));
    }

    @Test
    public void passwordNotMatch() throws Exception {

        Map<String, String> registerDto = createRegisterData();
        registerDto.put("password", "1234567");
        registerDto.put("confirmPassword", "1234568");
        JSONObject jsonObject = new JSONObject(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Passwords don't match")));
    }

    @Test
    public void genderNotSelected() throws Exception {

        Map<String, String> registerDto = createRegisterData();
        registerDto.put("gender", "UNSELECTED");
        JSONObject jsonObject = new JSONObject(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Please select gender")));
    }


    @Test
    public void register() throws Exception {


        prepareDatabase();
        Map<String, String> registerDto = createRegisterData();
        JSONObject jsonObject = new JSONObject(registerDto);

        User user = userRepository.findByApprovalCode(registerDto.get("approvalCode"));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        User updatedUser = userRepository.findById(user.getId()).get();
        assertEquals(null, updatedUser.getApprovalCode());
        assertNotNull(updatedUser.getPassword());
        assertNotNull(updatedUser.getGender());
        assertTrue(updatedUser.getEnabled());
        assertEquals(1, updatedUser.getCity().getId());


        List<Reference> newReference = referenceRepository.findByParent(updatedUser);
        assertTrue(newReference.size() > 0);



    }
}



















