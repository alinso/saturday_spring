package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
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
    UserService userService;

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

    @GetMapping("updatePoint/")
    public ResponseEntity<?> updatePoint() {

        List<User> all = userRepository.findAll();
        List<User> toBeSaved = new ArrayList<>();

        int i = 0;
        for (User u : all) {
            Integer p = userService.calculateUserPoint(u);
            u.setPoint(p);
            toBeSaved.add(u);
            i++;
            if (i % 50 == 0) {
                userRepository.saveAll(toBeSaved);
                toBeSaved.clear();
            }
        }
        return new ResponseEntity<String>("guncelendi "+i, HttpStatus.OK);
    }


    @GetMapping("autoMessage")
    public ResponseEntity<?> autoMessage(){
        List<User> selectedUsers = userRepository.findAllWomen(Gender.FEMALE);
        User tuuce  =userRepository.getOne(Long.valueOf(33));

        List<Message> toBeSaved  =new ArrayList<>();
        int i=0;
        for(User u:selectedUsers){
            String messageText = "15 Haziranda İncek'te bir havuzlu villa kiralayıp mangal partisi yapacağız. Activity Friend ve üyemiz Halil birlikteliği ile düzenlediğimiz bu aktivite ve her zamanki gibi güvenli,kaliteli bir topluluk ile keyifli zaman geçireceğimiz unutulmaz bir gün olacak. " +
                    "Ayrıca bu zamana kadar yapılmış olan en büyük AF aktivitesi olacak. Gelecek olan kişiler yine aynı şekilde Halil'in aktivitesine istek atacak ve aralarından seçeceğiz.  Kişileri özel olarak seçecek ve kadın/erkek sayısal dengesini dikkate alacağız. " +
                    "Gerekli güvenlik önlemlerinin alınacağı partimizde çeşitli oyunlar-eğlenceler bizi bekliyor olacak. Daha fazla detay için soru sorabilir veya aktiviteye istek atabilirsin. Bu günü kaçırma!";
            Message message =  new Message();
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

        userService.deleteByIdAdmin(id);
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }

    @GetMapping("updateUser/{id}")
    public ResponseEntity<String> updateUSer(@PathVariable("id") Long id) {
        adminService.updateInvalidUsername(id);
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


}