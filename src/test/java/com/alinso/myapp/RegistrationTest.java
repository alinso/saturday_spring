package com.alinso.myapp;

import com.alinso.myapp.entity.Application;
import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Reference;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.ApplicationStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.UserStatus;
import com.alinso.myapp.repository.ApplicationRepository;
import com.alinso.myapp.repository.CityRepository;
import com.alinso.myapp.repository.ReferenceRepository;
import com.alinso.myapp.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
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
    CityRepository cityRepository;

    @Autowired
    ReferenceRepository referenceRepository;

    @Autowired
    ApplicationRepository applicationRepository;



    private final String REFERRER_USER_NAME = "refereresdvdbagzxghvabshcxjhvbascgvhbsdaf";
    private final String APPLICATION_USER_NAME = "AKJGFAJKSDjksgafasjfhjgadhgasdfjgvsdafh";
    private final String APPLICATION_PHONE = "1234567899";
    private final String APPROVED_USER_PHONE = "1234267899";
    private final String REFERENCE_CODE = "12345";
    private final String APPROVAL_CODE="4scop0rhg3c";
    private final String USED_PHONE="1236547894";


    private void insertRefererUserToDb(){
        City c= cityRepository.findById(Long.valueOf(1)).get();
        User user  = new User();
        user.setCity(c);
        user.setInterests(null);
        user.setName(REFERRER_USER_NAME);
        user.setSurname("sur");
        user.setPhone(USED_PHONE);
        user.setGender(Gender.MALE);
        user.setPassword("skdjaklkakskjhjfadlad");
        user.setApprovalCode(null);
        user.setBirthDate(new Date());
        user.setStatus(UserStatus.REGISTERED);
        user.setEnabled(true);
        user.setAbout("about");
        user.setExtraPercent(0);
        user.setTooNegative(0);
        user.setMotivation("motivation");

        userRepository.save(user);

        Reference reference= new Reference();
        reference.setReferenceCode(REFERENCE_CODE);
        reference.setParent(user);

        referenceRepository.save(reference);
    }

    private void insertApprovedUserToDb(){
        User approvedUser  = new User();
        approvedUser.setInterests(null);
        approvedUser.setName(REFERRER_USER_NAME);
        approvedUser.setSurname("sur");
        approvedUser.setPhone(APPROVED_USER_PHONE);
        approvedUser.setApprovalCode(APPROVAL_CODE);
        approvedUser.setBirthDate(new Date());
        approvedUser.setStatus(UserStatus.APPROVED);
        approvedUser.setEnabled(false);
        approvedUser.setAbout("about");
        approvedUser.setExtraPercent(0);
        approvedUser.setTooNegative(0);
        approvedUser.setMotivation("motivation");

        approvedUser.setPassword(null);
        approvedUser.setGender(null);
        approvedUser.setCity(null);

        userRepository.save(approvedUser);


        Reference reference= referenceRepository.findByCode(REFERENCE_CODE);
        reference.setReferenceCode(REFERENCE_CODE);
        reference.setChild(approvedUser);
        referenceRepository.save(reference);

    }

    @Test
    public  void clearDb() {

        List<Reference> references = referenceRepository.findReferencesByCode(REFERENCE_CODE);
        if(references!=null)
        referenceRepository.deleteAll(references);
        //delete referrer user
        List<User> users = userRepository.findByName(REFERRER_USER_NAME);
        userRepository.deleteAll(users);


        //delete applications
        List<Application> applications = applicationRepository.findByPhoneAndName(APPLICATION_PHONE, APPLICATION_USER_NAME);
        applicationRepository.deleteAll(applications);

        //delete approved user and reference codes
        List<User> approvedUsers = userRepository.findByName(APPLICATION_USER_NAME);
        //delete created reference after  register
        for(User u:approvedUsers){
            List<Reference> references2 = referenceRepository.findByParent(u);
            referenceRepository.deleteAll(references2);
        }
        userRepository.deleteAll(approvedUsers);

    }

    private Map<String, String> createApplication() {
        Map<String, String> map = new HashMap<>();
        map.put("name", APPLICATION_USER_NAME);
        map.put("surname", "test");
        map.put("phone", APPLICATION_PHONE);
        map.put("referenceCode", REFERENCE_CODE);
        map.put("about", "its all abut meen");
        return map;
    }

    private Map<String, String> createRegisterData() {
        Map<String, String> map = new HashMap<>();
        map.put("password", "123456");
        map.put("confirmPassword", "123456");
        map.put("gender", "1");
        map.put("approvalCode", APPROVAL_CODE);
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

        insertRefererUserToDb();
        Map<String, String> application = createApplication();
        application.put("phone", USED_PHONE);
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("This phone number is used")));

        clearDb();
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
        insertRefererUserToDb();
        Map<String, String> application = createApplication();
        application.put("referenceCode", "123");//it has to be 6 digits
        JSONObject jsonObject = new JSONObject(application);

        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Reference Code")));
        clearDb();
    }


    @Test
    public void saveWithReference() throws Exception {
        insertRefererUserToDb();
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

       clearDb();
    }

    @Test
    public void saveWithoutReference() throws Exception {

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

       clearDb();
    }

    @Test
    public void approveWithReference() throws Exception {
        insertRefererUserToDb();
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
        assertTrue(user.getStatus() == UserStatus.APPROVED);
        assertTrue(!user.isEnabled());

        Reference reference = referenceRepository.findByChild(user); //ever user has one single parent
        assertTrue(reference != null);


        clearDb();
    }

    @Test
    public void approveWithoutReference() throws Exception {


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
        assertTrue(user.getStatus() == UserStatus.APPROVED);
        assertTrue(!user.isEnabled());


        Reference reference = referenceRepository.findByChild(user); //ever user has one single parent
        assertTrue(reference == null);

       clearDb();
    }

    @Test
    public void multiApplicationsWithSameRefCode() throws Exception { //what about 2 applications with same tel no?
        /*
         * there ca be only one application with one ref code
         *
         * */
        insertRefererUserToDb();

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


        //create an application2 with ref code
        JSONObject jsonObject2 = new JSONObject(application2);
        mockMvc.perform(MockMvcRequestBuilders.post("/application/save")
                .content(jsonObject2.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Reference Code")));

        clearDb();
    }

    @Test
    public void declineApplication() throws Exception {

        insertRefererUserToDb();

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

        clearDb();
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
        insertRefererUserToDb();
        insertApprovedUserToDb();

        Map<String, String> registerDto = createRegisterData();
        JSONObject jsonObject = new JSONObject(registerDto);

        User user = userRepository.findByApprovalCode(registerDto.get("approvalCode"));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andDo(
                new ResultHandler() {
                    @Override
                    public void handle(MvcResult mvcResult) throws Exception {
                        User updatedUser = userRepository.findById(user.getId()).get();
                        assertEquals(null, updatedUser.getApprovalCode());
                        assertTrue(updatedUser.getPassword()!=null);
                        assertTrue(updatedUser.getGender()!=null);
                        assertEquals(1, updatedUser.getCity().getId());
                        assertEquals(updatedUser.getStatus(),UserStatus.REGISTERED);
                        assertTrue(!user.isEnabled());

                        List<Reference> newReference = referenceRepository.findByParent(updatedUser);
                        assertTrue(newReference.size() > 0);
                    }
                }
        );
        clearDb();
    }
}



















