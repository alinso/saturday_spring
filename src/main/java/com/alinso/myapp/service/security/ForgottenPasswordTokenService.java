package com.alinso.myapp.service.security;

import com.alinso.myapp.entity.ForgottenPasswordToken;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.mail.util.SMTPMailUtil;
import com.alinso.myapp.repository.ForgottenPasswordTokenRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ForgottenPasswordTokenService {

    @Autowired
    ForgottenPasswordTokenRepository forgottenPasswordTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${application.url}")
    private String applicationUrl;

    public String saveToken(User user){
        String token = UUID.randomUUID().toString();
        try {
            ForgottenPasswordToken oldToken  =forgottenPasswordTokenRepository.findByUser(user).get();
            forgottenPasswordTokenRepository.delete(oldToken);
        }catch (NoSuchElementException e){

        }

        ForgottenPasswordToken forgottenPasswordToken = new ForgottenPasswordToken();
        forgottenPasswordToken.setToken(token);
        forgottenPasswordToken.setUser(user);

        forgottenPasswordTokenRepository.save(forgottenPasswordToken);
        return token;

    }


//    public void sendMail(String token,String recipientAddress){
//        String subject = "Night Out Şifremi Unuttum!";
//        String confirmationUrl = applicationUrl+ "/resetPassword/" + token;
//
//        SMTPMailUtil email = new SMTPMailUtil();
//        email.setRecipient(recipientAddress);
//        email.setSubject(subject);
//        email.setMessage(" rn" + "http://localhost:8080" + confirmationUrl);
//        email.sendMail();
//    }



    public ForgottenPasswordToken findByToken(String tokenString){
        ForgottenPasswordToken token;
        try {
            token = forgottenPasswordTokenRepository.findByToken(tokenString).get();
        }catch (NoSuchElementException e){
            throw new UserWarningException("Geçersiz Token! 'Şifremi Unuttum' kısmından tekar mail giriniz");
        }
        return token;
    }


    public void delete(ForgottenPasswordToken token) {
        forgottenPasswordTokenRepository.delete(token);
    }
}
