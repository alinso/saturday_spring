package com.alinso.myapp.service;

import com.alinso.myapp.dto.reference.ReferenceDto;
import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Reference;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.ReferenceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ReferenceService {

    @Autowired
    ReferenceRepository referenceRepository;

    @Autowired
    ModelMapper modelMapper;

    public void createNewReferenceCodes(User parent){
        for(int i=0;i<5;i++){
            Reference reference =  new Reference();
            reference.setParent(parent);
            reference.setReferenceCode(makeReferenceCode());
            referenceRepository.save(reference);
        }
    }


    public String makeReferenceCode() {
        Character[] characterArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'y', 'z',
                 '1', '2', '3', '4', '5', '6', '8', '9', '0'};

        Random rnd = new Random();
        Character c1 = characterArray[rnd.nextInt(31)];
        Character c2 = characterArray[rnd.nextInt(31)];
        Character c3 = characterArray[rnd.nextInt(31)];
        Character c4 = characterArray[rnd.nextInt(31)];
        Character c5 = characterArray[rnd.nextInt(31)];
        Character c6 = characterArray[rnd.nextInt(31)];

        String newName = c1.toString() + c2.toString() + c4.toString() + c3.toString() + c5.toString() + c6.toString();
        return newName;
    }


    public List<ReferenceDto> getMyReferences() {
        User loggedUser  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Reference> references = referenceRepository.findByParent(loggedUser);
        List<ReferenceDto> referenceDtos =new ArrayList<>();

        for (Reference reference:references){
            ReferenceDto referenceDto =  new ReferenceDto();
            if(reference.getChild()!=null) {
                ProfileDto profileDto = modelMapper.map(reference.getChild(), ProfileDto.class);
                referenceDto.setChild(profileDto);
            }
            referenceDto.setReferenceCode(reference.getReferenceCode());
            referenceDtos.add(referenceDto);
        }

        return referenceDtos;

    }

    public ReferenceDto findByCode(String referenceCode) {
        ReferenceDto referenceDto= null;
        Reference reference= referenceRepository.findByCode(referenceCode);
        if(reference!=null)
        referenceDto = modelMapper.map(reference, ReferenceDto.class);
        return referenceDto;
    }

    public void useReferenceCode(User child) {
        Reference reference= referenceRepository.findByCode(child.getReferenceCode());
        reference.setChild(child);
        referenceRepository.save(reference);
    }
}


















