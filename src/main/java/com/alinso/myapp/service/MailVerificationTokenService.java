package com.alinso.myapp.service;

import com.alinso.myapp.entity.MailVerificationToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.mail.SMTPEmail;
import com.alinso.myapp.repository.MailVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class MailVerificationTokenService {
    private static final int EXPIRATION = 60 * 24;

    @Autowired
    MailVerificationTokenRepository mailVerificationTokenRepository;

    @Value("${application.url}")
    private String applicationUrl;

    public String saveToken(User user){
        String token = UUID.randomUUID().toString();
        MailVerificationToken mailVerificationToken =  new MailVerificationToken();
        mailVerificationToken.setToken(token);
        mailVerificationToken.setUser(user);

        mailVerificationTokenRepository.save(mailVerificationToken);
        return token;

    }


    public void sendMail(String token,String recipientAddress){
        String subject = "Registration Confirmation";
        String confirmationUrl = applicationUrl+ "/verifyMail/" + token;

        SMTPEmail email = new SMTPEmail();
        email.setRecipient(recipientAddress);
        email.setSubject(subject);
        email.setMessage(" rn" + "http://localhost:8080" + confirmationUrl);
        email.sendMail();
    }



    public MailVerificationToken findByToken(String token){
        return mailVerificationTokenRepository.findByToken(token).get();
    }


}
