package com.alinso.myapp;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void isRequiredFieldsSatisfied() throws Exception {

        Map<String,String> user =  new HashMap<>();
        user.put("name","");
        user.put("surname","");
        user.put("gender","MALE");
        user.put("password","");
        user.put("confirmPassword","123123");
        user.put("referenceCode","");
        user.put("email","");

        JSONObject jsonObject = new JSONObject(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("İsim boş olamaz")))
                .andExpect(content().string(containsString("Soyisim boş olamaz")))
                .andExpect(content().string(containsString("Email adresi boş olamaz")))
                .andExpect(content().string(containsString("Referansınız olmadan")));
    }


    @Test
    public void isEmailFormatCorrect() throws Exception {

        Map<String,String> user =  new HashMap<>();
        user.put("name","ali");
        user.put("surname","ali1");
        user.put("gender","MALE");
        user.put("password","123123");
        user.put("confirmPassword","123123");
        user.put("referenceCode","asdf");
        user.put("email","asdasd");

        JSONObject jsonObject = new JSONObject(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Geçerli bir email")));
    }


    @Test
    public void isPasswordsSame() throws Exception {

        Map<String,String> user =  new HashMap<>();
        user.put("name","ali");
        user.put("surname","ali1");
        user.put("gender","MALE");
        user.put("password","123123");
        user.put("confirmPassword","123123asd");
        user.put("referenceCode","asdf");
        user.put("email","asd@asd.com");

        JSONObject jsonObject = new JSONObject(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Şifreler eşleşmiyor")));
    }


    @Test
    public void isPasswordLongEnough() throws Exception {

        Map<String,String> user =  new HashMap<>();
        user.put("name","ali");
        user.put("surname","ali1");
        user.put("gender","MALE");
        user.put("password","123");
        user.put("confirmPassword","123");
        user.put("referenceCode","asdf");
        user.put("email","asdasd@asd.com");

        JSONObject jsonObject = new JSONObject(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("en az 6 karakter")));
    }

    @Test
    public void crud() throws Exception {

        //create
        Random random = new Random();
        Integer first = random.nextInt(99999);
        Integer second = random.nextInt(99999);
        Integer third = random.nextInt(99999);
        String email = "mail" + first.toString() + second.toString() + "@mail" + third.toString() + ".com";

        Map<String, String> user = new HashMap<>();
        user.put("name", "ali");
        user.put("surname", "ali1");
        user.put("gender", "MALE");
        user.put("password", "123456");
        user.put("confirmPassword", "123456");
        user.put("referenceCode", "asdf");
        user.put("email", email);

        JSONObject jsonObject = new JSONObject(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        //read
        String responseStr = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        User user1 = objectMapper.readValue(responseStr, User.class);

        User userInDb = userRepository.findById(user1.getId()).get();
        Assert.isTrue(userInDb.getEmail().equals(user1.getEmail()) && userInDb.getName().equals(user1.getName()));

        //update

        Map<String, String> userDto = new HashMap<>();
        userDto.put("id", String.valueOf(userInDb.getId()));
        userDto.put("name", "nameupdated");
        userDto.put("surname", "surnameupdated");
        userDto.put("gender", "FEMALE");
        userDto.put("referenceCode", "asdf");
        userDto.put("email", "asdasd@asd.com");
        userDto.put("about", "asdasd@asd.com");
        userDto.put("rate", "3.0");
        userDto.put("eventcount", "5");

        JSONObject jsonObjectUpdate = new JSONObject(userDto);

        MvcResult resultUpdate = mockMvc.perform(MockMvcRequestBuilders.post("/user/update")
                .content(jsonObjectUpdate.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("surname")))
                .andReturn();


        //delete
        mockMvc.perform(MockMvcRequestBuilders.get("/user/delete/"+userInDb.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        mockMvc.perform(MockMvcRequestBuilders.get("/user/id/"+userInDb.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("userNotFound")));

    }


    @Test
    public void isNonExistUserCanBeUpdated() throws Exception {
        Map<String, String> userDto = new HashMap<>();
        userDto.put("id", "0");
        userDto.put("name", "nameupdated");
        userDto.put("surname", "surnameupdated");
        userDto.put("gender", "FEMALE");
        userDto.put("referenceCode", "asdf");
        userDto.put("email", "asdasd@asd.com");
        userDto.put("about", "asdasd@asd.com");
        userDto.put("rate", "3.0");
        userDto.put("eventcount", "5");

        JSONObject jsonObjectUpdate = new JSONObject(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/update")
                .content(jsonObjectUpdate.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("userNotFound")))
                .andReturn();
    }

    @Test
    public void isNonExistUserCanBeDeleted() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.get("/user/delete/0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("userNotFound")))
                .andReturn();
    }

}
