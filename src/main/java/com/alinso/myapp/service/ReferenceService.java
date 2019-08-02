package com.alinso.myapp.service;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class ReferenceService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    public void createNewReferenceCode(User parent) {
        parent.setReferenceCode(makeReferenceCode());
        userRepository.save(parent);
    }


    public String makeReferenceCode() {
        Character[] characterArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'y', 'z',
                '1', '2', '3', '4', '5', '6', '8', '9', '0', '_', '-'};

        Random rnd = new Random();
        Character c1 = characterArray[rnd.nextInt(33)];
        Character c2 = characterArray[rnd.nextInt(33)];
        Character c3 = characterArray[rnd.nextInt(33)];
        Character c4 = characterArray[rnd.nextInt(33)];
        Character c5 = characterArray[rnd.nextInt(33)];
        Character c6 = characterArray[rnd.nextInt(33)];

        String newName = c1.toString() + c2.toString() + c4.toString() + c3.toString() + c5.toString() + c6.toString();
        return newName;
    }


    public List<ProfileDto> getMyReferences() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> references = userRepository.findByParent(loggedUser);
        return userService.toProfileDtoList(references);
    }

    public User findByCode(String referenceCode) {
        User user = userRepository.findByReferenceCode(referenceCode);
        return user;
    }

    public List<ProfileDto> getChildrenOfParent(User parent) {
        List<User> references = userRepository.findByParent(parent);
        return userService.toProfileDtoList(references);
    }
}


















