package com.alinso.myapp.service;

import com.alinso.myapp.entity.Complain;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.complain.ComplainDto;
import com.alinso.myapp.repository.ComplainRepository;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ComplainService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ComplainRepository complainRepository;

    public void create(ComplainDto complainDto) {
        User reporter  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User guilty  =userRepository.getOne(complainDto.getGuiltyId());

        Complain complain =  new Complain();
        complain.setGuilty(guilty);
        complain.setReporter(reporter);
        complain.setDetail(complainDto.getDetail());

        complainRepository.save(complain);
    }

}
