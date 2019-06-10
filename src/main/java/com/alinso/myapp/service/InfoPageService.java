package com.alinso.myapp.service;

import com.alinso.myapp.entity.InfoPage;
import com.alinso.myapp.repository.InfoPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoPageService {

    @Autowired
    InfoPageRepository infoPageRepository;

    public void update(InfoPage infoPage){
            infoPageRepository.save(infoPage);
    }

    public InfoPage findById(Long id){
        InfoPage infoPage = infoPageRepository.findById(id).get();
        return infoPage;
    }

}
