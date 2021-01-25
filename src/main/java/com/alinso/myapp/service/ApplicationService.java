package com.alinso.myapp.service;


import com.alinso.myapp.entity.Application;
import com.alinso.myapp.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    ApplicationRepository applicationRepository;

    public void save(Application a){
        applicationRepository.save(a);
    }


}
