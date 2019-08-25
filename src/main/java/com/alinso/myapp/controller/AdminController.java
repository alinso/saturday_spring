package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Activity;
import com.alinso.myapp.entity.Complain;
import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.entity.enums.ActivityRequestStatus;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.repository.ActivityRepository;
import com.alinso.myapp.repository.ActivityRequesRepository;
import com.alinso.myapp.repository.MessageRepository;
import com.alinso.myapp.repository.UserRepository;
import com.alinso.myapp.service.AdminService;
import com.alinso.myapp.service.DiscoverService;
import com.alinso.myapp.service.UserEventService;
import com.alinso.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("lkaldjfnuterhjbsfsdf")
@RestController
public class AdminController {

    @Autowired
    DiscoverService discoverService;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    UserEventService userEventService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AdminService adminService;

    @GetMapping("dashboard")
    public ResponseEntity<?> dashboard() {
        return new ResponseEntity<String>("okk", HttpStatus.OK);
    }



    @GetMapping("autoMessage/{page}")
    public ResponseEntity<?> autoMessage(@PathVariable("page") Integer page) {
        List<User> selectedUsers = userRepository.findAllWomen(Gender.FEMALE);

        Activity activity =activityRepository.findById(Long.valueOf(3775)).get();
        List<User> attendedUsers = activityRequesRepository.attendantsOfActivity(activity, ActivityRequestStatus.APPROVED);


        User tuuce = userRepository.getOne(Long.valueOf(3212));

        List<Message> toBeSaved = new ArrayList<>();
        int i = 0;
        for (User u : selectedUsers) {
            i++;


            if(i<((page-1)*500))
                continue;
            if (i > (page*500))
                break;



            Boolean attended=false;
            for(User attendedUser:attendedUsers){
                if(attendedUser.getId()==u.getId()) {
                    attended = true;
                    break;
                }
            }

            if(attended)
                continue;


            String messageText = "Activity Friend partisine bir gün kaldı, sen daha istek göndermedin mi? \n" +
                    "\n" +
                    "İlk gelen girişte 100 kişiye ücretsiz shot, dans gösterileri, yarışmalar ve elbette sınırsız müzik ve dans seni bekliyor.\n" +
                    "\n" +
                    "Ağustosun en eğlenceli aktivitesine katılmak için acele et ve AF üyelerine özel bu gecede sen de aramızda ol.";


            Message message = new Message();
                message.setReader(u);
                message.setWriter(tuuce);
                message.setMessage(messageText);
                messageRepository.save(message);
                userEventService.newMessage(message.getReader());

        }
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }


    @PostMapping("createDiscover")
    public ResponseEntity<?> saveEvent(@Valid DiscoverDto discoverDto) {
        discoverService.save(discoverDto);
        return new ResponseEntity<>("okk", HttpStatus.OK);
    }

    @PostMapping("updateDiscover")
    public ResponseEntity<?> updateDiscover(@Valid DiscoverDto discoverDto) {
        discoverService.update(discoverDto);
        return new ResponseEntity<>("okk", HttpStatus.OK);
    }

    @GetMapping("deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {

        adminService.deleteByIdAdmin(id);
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }

    @GetMapping("userInfo/{id}")
    public ResponseEntity<User> userInfo(@PathVariable("id") Long id) {
        User user = adminService.userInfo(id);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @GetMapping("resetPassword/{id}")
    public ResponseEntity<String> resetPassword(@PathVariable("id") Long id) {
        adminService.resetPassword(id);
        return new ResponseEntity<>("okk", HttpStatus.OK);
    }

    @GetMapping("allComplaints")
    public ResponseEntity<?> getAllComplaints(){
        List<Complain> complainList  =adminService.getAllComplaints();
        return new ResponseEntity<>(complainList,HttpStatus.OK);
    }


}