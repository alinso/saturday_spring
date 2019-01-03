package com.alinso.myapp.service.security;

import com.alinso.myapp.entity.MailVerificationToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.mail.SMTPEmail;
import com.alinso.myapp.repository.MailVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MailVerificationTokenService {

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
        String subject = "Night Out E-Posta Doğrulama";
        String confirmationUrl = applicationUrl+ "/verifyMail/" + token;

        SMTPEmail email = new SMTPEmail();
        email.setRecipient(recipientAddress);
        email.setSubject(subject);
        email.setMessage(" rn" + "http://localhost:8080" + confirmationUrl);
        email.sendMail();
    }



    public MailVerificationToken findByToken(String token){
        try {
            return mailVerificationTokenRepository.findByToken(token).get();
        }catch (NoSuchElementException e){
            throw new UserWarningException("Geçersiz Link");
        }
    }


}
