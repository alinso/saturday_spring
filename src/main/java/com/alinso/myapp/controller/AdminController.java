package com.alinso.myapp.controller;

import com.alinso.myapp.entity.*;
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

    @Autowired
    CityRepository cityRepository;

    @GetMapping("dashboard")
    public ResponseEntity<?> dashboard() {
        return new ResponseEntity<String>("okk", HttpStatus.OK);
    }


    @GetMapping("autoMessage/{page}")
    public ResponseEntity<?> autoMessage(@PathVariable("page") Integer page) {


//        String sDate1="08/08/2019";
//        Date date1= null;
//        try {
//            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        City city = cityRepository.findById(Long.valueOf(1)).get();
        List<User> selectedUsers = userRepository.allOfACity(city);
        if (page == 0)
            page = 1;

        User tuuce = userRepository.getOne(Long.valueOf(3212));
        // List<Message> toBeSaved = new ArrayList<>();
        int i = 0;
        for (User u : selectedUsers) {
            i++;


            if (i < ((page - 1) * 500))
                continue;
            if (i > (page * 500))
                break;


//            String messageText = u.getName()+", selamlar, Nelerden hoşlanırsan onunla ilgili bir aktivite oluştur, aktivitene katılmak isteyen kişilerden dilediğini onaylayabilirsin ve" +
//                    " sonrasında mesajlaşabilirsiniz. Şu an Eskişehir üye sayımız 300'ü geçti(çok büyük çoğunluğu kadın), büyüyoruz. Seninle aynı zevkleri paylaşan insanlarla tanışabilirsin. Nasıl bir aktivite açacağına karar " +
//                    "veremiyorsan Ankara'da açılmış olanları inceleyebilirsin. Aktiviteler çok eğlenceli geçiyor, merak etme birbirini hiç tanımayan insanlar bir" +
//                    "aktivite etrafında buluştuğunda çok keyfili olabiliyor. Şimdi her ne yapmak istiyorsan bir aktivite oluştur ve eğlenceye katıl!";

                        String messageText = u.getName()+", selamlar, Geçen cuma 100 kişiden fazlaydik ve çok eğlendik bu Cuma(25 Ekim'de) Halloween temalı parti yapıyoruz. Özellikle yeni katılan veya " +
                                " aktif olamayan arkadaşların bizi tanıyıp güve duyması için önemli bir fırsatYalnız Activity Friend kullanıcıları orada oalcak. " +
                                " kostümün varsa kostümlü gel, yoksa da isteyenlere girişte halloween makyaj yapacağız saat 21'de DJ Akın başlıyor Vee hep birlikte macarena kareografisi yapacağız." +
                                " Profesyonel fotoğrafçımız gecenin fotoğraflarını çekiyor. Giriş  yalnızca 5TL" +
                                " Şimdi aktviteme istek at, bunu kaçırma";

//            String messageText = "Merhaba "+ u.getName()+", sınırlara takılamdan aktivite açabilmek ve katılmak istersen premium olabilirsin. Detayları ana sayfada en tepedeki 'premium hakkında" +
//                    " bilgilendirme' linkine tıklayarak görebilirsin. Premium hesaplar diğer üyelere güven veriyor, bu nedenle açtıkları aktivitelere katılım ve kabul edilme oranları standart " +
//                    "kullanıcılara göre daha yüksek. İnsanlar premium kullanıcıların platformu daha ciddiye aldığını düşünüyor. Ayrıca yarın ilkini yapacağımız cuma partilerine gold üyeler " +
//                    "ücretsiz olarak katılabiliecek. Activity Friend projesi sürekli büyüyor ve gelişiyor, fakat aynı şekilde sürekli olarak büyüyen masraflarla karşı karşıyayız. Bu süreçte premium olarak bize destek" +
//                    " olabilirsin. Teşekkürler";

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