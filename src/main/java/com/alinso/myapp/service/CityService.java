package com.alinso.myapp.service;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
@Autowired
    CityRepository cityRepository;

    public List<City> findAllOrderByName(){
        return cityRepository.findAllOrderByName();
    }

    public City findById(Long id){
        try {
            return cityRepository.findById(id).get();
        }catch (Exception e){
            throw  new UserWarningException("Şehir bulunamadı");
        }
    }

}
