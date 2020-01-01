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
        List<User> selectedUsers = userRepository.allAnkaraWomen(city,Gender.FEMALE);

        Activity activity=activityRepository.findById(Long.valueOf(6444)).get();
        List<User> applicants = activityRequesRepository.applicantsOfActivity(activity);
        if (page == 0)
            page = 1;

        User tuuce = userRepository.getOne(Long.valueOf(3212));
        // List<Message> toBeSaved = new ArrayList<>();
        int i = 0;
        for (User u : selectedUsers) {
            i++;

//            Boolean alreadyAttended=false;
//            for(User applicant:applicants){
//                if(applicant.getId()==u.getId()){
//                    alreadyAttended=true;
//                }
//            }
//
//            if(alreadyAttended)
//                continue;

            if (i < ((page - 1) * 500))
                continue;
            if (i > (page * 500))
                break;


            String messageText =  u.getName()+" selamlar nasılsın, aktiviteler ve uygulama ile ilgili bazı hatırlatmalar yapmak istiyorum. topluluğumuz ortak ilgi alanındaki kişileri bir araya getirip" +
                    " 6-8 kişilik aktiviteler yapılması amacıyla kurulmuştur. Sen de herhangi bir aktivite oluşturabilir, dilediğin kişileri onaylayarak aktiviteni gerçekleştirebilirsin. Sadece onayladığın kişiler" +
                    " sana mesaj gönderebilir. Karmaşık, ilgi çekici, kalabalıklara hitap eden aktiviteler yapmak zorunda değilsin, sadece yürüyüş yapmak, bir kitap hakkında konusmak veya konsere gitmek gibi" +
                    " birkaç kişinin katılımı ile gerçekleşen aktiviteler açabilirsin. katılımcı sayısı 6-8 kişiden fazla olduğunda organize etmek çok zorlaşır ve katılımcılar birbiriyle fazla iletişim kuramaz, kaynaşamaz" +
                    " Aktivite sonrası mutlaka katılımcılar ile ilgili olumlu-olumsuz oy ver, gelmeyenleri yoklamada işaretle. Bu şekilde kullanıcı kitlemiz her zaman belirli bir kalitenin üzerinde kalacak." +
                    " Herhangi bir aktiviteye istek atarken veya aktivitene kabul ederken ilgili profilin olumlu izlenim oranını dikkate almalısın. Olumlu izlenim oranı düşük profiller katıldıkları aktivitelerde diğer" +
                    " kulanıcılar tarafından olumsuz olarak değerlendirilmiştir. Aklına takılan birsey olursa mutlaka bize sor, problemli bir durumla karşılaşır veya şüphe duyarsan bize bildir. Activity Friend güvenli," +
                    " kaliteli ve temiz kalsın. Bunu ancak toplu şekilde hareket ederek başarabiliriz. Farkında mısın, Ankara'da gerçekten birşeyleri değiştirdik ve bunu birlikte yaptık";

//

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