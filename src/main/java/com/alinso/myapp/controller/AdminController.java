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
import com.alinso.myapp.service.PremiumService;
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

    @Autowired
    DiscoverService discoverService;

    @Autowired
    ActivityRequesRepository activityRequesRepository;

    @Autowired
    PremiumService premiumService;

    @Autowired
    PremiumRepository premiumRepository;

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

    @Autowired
    CityRepository cityRepository;

    @GetMapping("dashboard")
    public ResponseEntity<?> dashboard() {
        return new ResponseEntity<String>("okk", HttpStatus.OK);
    }


    @GetMapping("autoMessage/{page}")
    public ResponseEntity<?> autoMessage(@PathVariable("page") Integer page) {



        City city = cityRepository.findById(Long.valueOf(1)).get();
        List<User> selectedUsers = userRepository.allOfACity(city);

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


            String messageText =  u.getName()+", yardımına ihtiyacımız var. Biliyorsun kadın-erkek dengesini koruyarak büyüyor ve kadınların azınlıkta kalmasını istemiyoruz. Her 4 yeni kadın üye alımı" +
                    " sonrası özenle seçerek bir erkek üye alımı yapıyoruz. Kadı nüye sayısı erkeğin 4 katı olduğunda aktivitelerde dengeli bir sayı elde edilebiliyor. Şu an üye olmak için bekleyen çok sayıda erkek " +
                    " var. Fakat sayısal dengeyi bozmak istemediğimiz için alamıyoruz. Sağlıklı bir şekilde büyüyebilmek için yeni kadın üyelere ihtiyacımız var. Activuss'a DEĞER KATABİLECEK kadın " +
                    " arkadaşlarına anlatıp onların da topluluğumuza katılmasını sağlarsan Acttivuss'u bozmadan büyütme şansımız olur. Daha büyük Activuss demek, daha fazla ve çeşitli aktiviteler, " +
                    " daha fazla fırsat, daha fazla çevre ve eğlence demek. Bu hepimiz için çok faydalı olur. Yaşadığımız şehirde hepimiz geniş bir çevre, yeni dostluklar ve deneyimler edindik." +
                    " Sadece birkaç arkadaşına bile anlatsan çok şey değişir.";


            Message message = new Message();
            message.setReader(u);
            message.setWriter(tuuce);
            message.setMessage(messageText);
            messageRepository.save(message);
            userEventService.newMessage(message.getReader());

        }
        return new ResponseEntity<>("okk", HttpStatus.OK);

    }

    @GetMapping("deletePartyVotes")
    public ResponseEntity<?> deletePartyVotes(@Valid DiscoverDto discoverDto) {
        List<Long> votes = adminService.deletePartyVotes();
        return new ResponseEntity<>(votes, HttpStatus.OK);
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
    public ResponseEntity<?> getAllComplaints() {
        List<Complain> complainList = adminService.getAllComplaints();


        return new ResponseEntity<>(complainList, HttpStatus.OK);
    }


}