package com.alinso.myapp.controller;

import com.alinso.myapp.entity.*;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.entity.enums.Gender;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.entity.enums.PremiumType;
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
import java.util.Date;
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


            String messageText =  u.getName()+" merhaba, Bu cumartesi Salsa Ankara'nın harika manzaralı kapalı terasında sıcacık sobamızın başında sakin ve dinlendirici müzikler eşliğinde şarap gecesi yapıyoruz. Herkesin şarabını alıp geleceği" +
                    " tanışıp sohbet edeceğimiz, birbirimize şarap ikram edip sosyalleşeceğimiz kaliteli, masalsı bir atmosfer yaratıyoruz. Özellikle aramıza yeni katılmış veya aktif olamamış " +
                    " arkadaşlarımız için ortamımızı görmeleri adına çok güzel bir fırsat. Eğer yalnız gelirsen birçok güzel insanla tanışacaksın, Activity Friend samimi ve nezih bir topluluk" +
                    " Şarap sevmiyorsan başka içecekler de getirebilirsin. Saat 19:00-20:30 arasında tanışıp sohbet edecek, sonrasında ise DJ Akın ile dans müziğe" +
                    " doyacağız. Eve erken dönmesi gereken arkadaşlarımız da dans edebilesin diye DJ performans saat 20:30'da başlıyor. Bu cumartesi en çok biz eğleneceğiz, bunu kaçırma! Katılım için aktiviteme istek atman yeterli" +
                    " Kadın-erkek sayısı dengeli olması kaydıyla üye olmayan arkadşlarınla da grup olarak gelebilirsin. Aklına takılan olursa lütfen sormaktan çekinme." +
                    " Başlangıç:19:00, Giriş 20 TL(Gold üyeler 10 TL)";


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