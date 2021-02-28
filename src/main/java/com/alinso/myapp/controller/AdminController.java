package com.alinso.myapp.controller;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Complain;
import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.repository.*;
import com.alinso.myapp.service.AdminService;
import com.alinso.myapp.service.DiscoverService;
import com.alinso.myapp.service.UserEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("lkaldjfnuterhjbsfsdf")
@RestController
public class AdminController {


    DiscoverService discoverService;
    EventRequestRepository eventRequestRepository;
    UserEventService userEventService;
    EventRepository eventRepository;
    MessageRepository messageRepository;
    UserRepository userRepository;
    AdminService adminService;
    CityRepository cityRepository;

    @Autowired
    public AdminController(DiscoverService discoverService, EventRequestRepository eventRequestRepository, UserEventService userEventService, EventRepository eventRepository, MessageRepository messageRepository, UserRepository userRepository, AdminService adminService, CityRepository cityRepository) {
        this.discoverService = discoverService;
        this.eventRequestRepository = eventRequestRepository;
        this.userEventService = userEventService;
        this.eventRepository = eventRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.cityRepository = cityRepository;
    }

    @GetMapping("dashboard")
    public ResponseEntity<?> dashboard() {
        return new ResponseEntity<String>("okk", HttpStatus.OK);
    }


    @GetMapping("autoMessage/{page}")
    public ResponseEntity<?> autoMessage(@PathVariable("page") Integer page) {



        City city = cityRepository.findById(Long.valueOf(1)).get();
        List<User> selectedUsers = userRepository.findAllWomen(Gender.FEMALE);

        if (page == 0)
            page = 1;

        User tuuce = userRepository.getOne(Long.valueOf(3212));
        int i = 0;
        for (User u : selectedUsers) {
            i++;



            if (i < ((page - 1) * 500))
                continue;
            if (i > (page * 500))
                break;


            String messageText = u.getName()+" merhaba, Activuss kadınların güçlü ve çoğunlukta olduğu bir platform. Burada sizleri rahatsız edebilecek durumların olmaması ve topluluğumuzu eviniz" +
                    " gibi hissetmeniz için elimizden geleni yapıyoruz. Sen de aktivite açarak veya katılım sağlayarak şehrimizdeki güçlü kadınlar misyonumuzda bize" +
                    " destek olabilirsin. Ne kadar aktif olurak o kadar güçlü oluruz. Aklına gelen veya takılan bir şey olursa bize yazmaktan çekinme. ";


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

    @GetMapping("resetPassword/{id}")
    public ResponseEntity<String> resetPassword(@PathVariable("id") Long id) {
        adminService.resetPassword(id);
        return new ResponseEntity<>("okk", HttpStatus.OK);
    }

    @GetMapping("allComplaints")
    public ResponseEntity<?> getAllComplaints() {
        List<Complain> complainList = adminService.getAllComplaints();


        return new ResponseEntity<>(complainList, HttpStatus.OK);
    }

    @GetMapping("updateInvalidUserName/{id}")
    public ResponseEntity<String> updateUSer(@PathVariable("id") Long id) {
        adminService.updateInvalidUsername(id);
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }

    @GetMapping("userInfo/{id}")
    public ResponseEntity<User> userInfo(@PathVariable("id") Long id) {
        User user = adminService.userInfo(id);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @GetMapping("updateExtraPoint/{id}/{extraPoint}")
    public ResponseEntity<String> updateExtraPoint(@PathVariable("id") Long id,@PathVariable("extraPoint") Integer extraPoint) {
        adminService.updateExtraPoint(id,extraPoint);
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }

}