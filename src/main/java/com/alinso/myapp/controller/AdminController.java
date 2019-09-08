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
        List<User> selectedUsers = userRepository.findAll();

        if(page==0)
            page=1;

        User tuuce = userRepository.getOne(Long.valueOf(3212));
       // List<Message> toBeSaved = new ArrayList<>();
        int i = 0;
        for (User u : selectedUsers) {
            i++;


            if(i<((page-1)*500))
                continue;
            if (i > (page*500))
                break;



//            String messageText = "Yarın akşam(28 Ağustos) saat 19'da Kızılay-Route'da buluşuyoruz. Sohbet edip, birşeyler içip tanışıyoruz" +
//                    " Aramıza yeni katılanlar veya aktif olamayanlar için topluluğumuzu tanımaları adına güzel bir fırsat. Hadi sen de gel," +
//                    " merak etme kimse birbirini tanımıyor fakat yine de konuşacak çok şeyimiz var. Katılmak istersen aktiviteme istek atabilirsin (kontenjan 30 kişi)";
            String messageText = "Merhaba "+u.getName()+", biliyorsun ki Activity Friend kullanıcı kalitesine çok dikkat ediyor. Kadın/Erkek cinsiyet oranının dengede olması da kalite anlayışımıza dahil. Kadınlar topluluğumuza ön yargılı yaklaşabiliyor, haklı olarak bilmedikleri bir ortam olduğu için güven sorunu yaşıyorlar. Bu konuda bize destek olabilir ve arkadaşlarına anlatıp onların da aramıza katılmalarını sağlayabilirsin. Burada çok güzel bir  topluluk olduk, bu güzelliği paylaşmamızda ve daha fazla kadına ulaşmamızda yardım et.\n" +
                    "\n" +
                    "                    Ayrıca profilim-referans ol sayfasındaki referans kodu ile kadınlara referans olup üye olmalarını sağlayarak puan kazanabilirsin. Senin referansınla üye olan her kadına başlangıçta 5 puan veriyoruz. Arkadaşın 10 puan olduğunda sana da 10 puan ekliyoruz.\n" +
                    "\n" +
                    "            Activity Friend'i bu günlere birlikte getirdik, bundan sonra da birlikte devam edeceğiz, bu serüvenin bir parçası olduğun için teşekkür ederiz";


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