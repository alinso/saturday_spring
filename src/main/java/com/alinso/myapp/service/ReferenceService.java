package com.alinso.myapp.service;

import com.alinso.myapp.entity.Reference;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.repository.ReferenceRepository;
import com.alinso.myapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    ReferenceRepository referenceRepository;


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
        List<User> myReferences  = getChildrenOfParent(loggedUser);
        return userService.toProfileDtoList(myReferences);
    }

    public User findParentByRefereceCode(String referenceCode) {
        Reference r = referenceRepository.findByCode(referenceCode);
        return r.getParent();
    }

    public List<User> getChildrenOfParent(User parent) {
        List<Reference> references = referenceRepository.findByParent(parent);
        List<User> users = new ArrayList<>();
        for (Reference r : references) {
            users.add(r.getChild());
        }
        return users;
    }

    public boolean isReferenceCodeValid(String referenceCode) {
        Reference reference = referenceRepository.getValidReference(referenceCode);

        if (reference == null)
            return false;
        else
            return true;
    }

}


















