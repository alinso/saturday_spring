package com.alinso.myapp.service;

import com.alinso.myapp.entity.Discover;
import com.alinso.myapp.entity.dto.event.DiscoverDto;
import com.alinso.myapp.repository.DiscoverRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DiscoverService {

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    ModelMapper modelMapper;


    @Autowired
    DiscoverRepository discoverRepository;

    public void save(DiscoverDto discoverDto) {

        Discover discover = modelMapper.map(discoverDto, Discover.class);
        discover.setDate(DateUtil.stringToDate(discoverDto.getDtString(), "dd/MM/yyyy HH:mm"));
        discover.setPhotoName(fileStorageUtil.saveFileAndReturnName(discoverDto.getFile()));

        discoverRepository.save(discover);
    }

    public List<DiscoverDto> findNonExpiredEvents(){
       List<Discover> discoverList =  discoverRepository.findNonExpiredEvents(new Date());
       List<DiscoverDto> discoverDtoList =  new ArrayList<>();
       for(Discover discover : discoverList){
           DiscoverDto discoverDto = modelMapper.map(discover, DiscoverDto.class);
           discoverDto.setDtString(DateUtil.dateToString(discover.getDate(),"dd/MM/yyyy HH:m"));
            discoverDtoList.add(discoverDto);
        }

        return discoverDtoList;
    }
}
