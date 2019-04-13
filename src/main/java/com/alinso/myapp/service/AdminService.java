package com.alinso.myapp.service;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.message.MessageDto;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageService messageService;


    public void updateInvalidUsername(Long id) {
        User user = userService.findEntityById(id);
        user.setName("GEÇERSİZ");
        user.setSurname("İSİM");
        userRepository.save(user);

        MessageDto messageDto = new MessageDto();
        messageDto.setReader(userService.toProfileDto(user));
        messageDto.setMessage("Merhaba:) Activity Friend içinde insanların gerçek-tam isimlerini kullanmaları gerektiğini düşünüyoruz. Böylece insanlar buluşmadan önce " +
                "birbirlerini internet/sosyal medyadan araştırma fırsatı bulabilir. Bu nedenle tam ismini kullanırsan seviniriz:)");

    messageService.send(messageDto);

    }
    public void resetPassword(Long id){
        User user = userService.findEntityById(id);
        user.setPassword("$2a$10$vbdDvwd/ZVsD1avjqUVzAOO7JNJm/6kj3xReWEWJfEQ9QnqGYXcO2");
        userRepository.save(user);
    }




    public User userInfo(Long id) {
        return  userService.findEntityById(id);
    }
}
