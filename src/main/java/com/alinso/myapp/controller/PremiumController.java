package com.alinso.myapp.controller;

import com.alinso.myapp.entity.Message;
import com.alinso.myapp.entity.Premium;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.message.MessageDto;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.entity.enums.PremiumDuration;
import com.alinso.myapp.entity.enums.PremiumType;
import com.alinso.myapp.service.*;
import com.alinso.myapp.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/premium")
public class PremiumController {

    @Autowired
    PremiumService premiumService;

    @Autowired
    DayActionService dayActionService;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    VibeService vibeService;

    @PostMapping("save/{userId}")
    public ResponseEntity<?> save(@RequestBody Premium premium, @PathVariable("userId") Long userId) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(loggedUser.getId()!=3212)
            return new ResponseEntity<>("hata", HttpStatus.OK);

        premium.setType(PremiumType.SOLD);
        premiumService.save(premium,userId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("checkActivityLimit")
    public ResponseEntity<?> checkActivityLimit() {
        dayActionService.checkActivityLimit();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("sellMePremium/{premiumDurationName}")
    public ResponseEntity<?> sellMePremium(@PathVariable("premiumDurationName") String premiumDurationName){


        User loggedUser= (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer vibe=vibeService.calculateVibe(loggedUser.getId());
        if(vibe<75 && vibe>4)
            return new ResponseEntity<>("OK", HttpStatus.OK);



        PremiumDuration premiumDuration= PremiumDuration.valueOf(premiumDurationName);
        Premium premium= new Premium();
        premium.setType(PremiumType.SOLD);
        premium.setDuration(premiumDuration);
        premiumService.save(premium,loggedUser.getId());


        String durationText="";
        if(premiumDuration==PremiumDuration.SONE_MONTH){
            durationText="19.90 TL değerinde 1 Aylık Silver";
        }
        if(premiumDuration==PremiumDuration.STHREE_MONTHS){
            durationText="49.90 TL değerinde 3 Aylık Silver";
        }
        if(premiumDuration==PremiumDuration.GONE_MONTH){
            durationText="29.90 TL değerinde 1 Aylık Gold";
        }
        if(premiumDuration==PremiumDuration.GTHREE_MONTHS){
            durationText="79.90 TL değerinde 3 Aylık Gold";
        }


        String fullText = durationText+ " premium üyelik hesabınıza tanımlanmıştır, bitiş tarihi profilinizde gözüküyor olmalı, çok teşekkür ederiz."+
                                    " Aşağıda IBAN numarası verilen hesaba EFT yapabilirsiniz, (Hesap Bilgisi : Ali İnsan Soyaslan / Garanti Bankası)";

        User superman = userService.findEntityById(Long.valueOf(3212));
        Message message = new Message();
        message.setReader(loggedUser);
        message.setWriter(superman);
        message.setMessage(fullText);
        messageService.sendPremiumMessage(message);

        Message iban= new Message();
        iban.setReader(loggedUser);
        iban.setWriter(superman);
        iban.setMessage("TR11 0006 2000 2950 0006 8400 70");
        messageService.sendPremiumMessage(iban);


        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @GetMapping("professionals")
    public ResponseEntity<?> professionals(){
     List<ProfileDto> users = premiumService.findProfessionals();
     return new ResponseEntity<>(users,HttpStatus.OK);
    }

    @GetMapping("latestPremiumDate")
    public ResponseEntity<?> latestPremiumDate() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String latestPremiumDate = DateUtil.dateToString(premiumService.getPremiumLastDate(user), "dd/MM/YYYY");
        return new ResponseEntity<String>(latestPremiumDate, HttpStatus.OK);
    }
}
