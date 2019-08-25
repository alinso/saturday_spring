package com.alinso.myapp.service;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Discover;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.repository.CityRepository;
import com.alinso.myapp.repository.DiscoverRepository;
import com.alinso.myapp.util.DateUtil;
import com.alinso.myapp.util.FileStorageUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class DiscoverService {

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    DiscoverRepository discoverRepository;

    public void save(DiscoverDto discoverDto) {
        City city=null;
        if(discoverDto.getCityId()!=0)
            city=cityRepository.getOne(discoverDto.getCityId());

        Discover discover = modelMapper.map(discoverDto, Discover.class);
        discover.setPhotoName(fileStorageUtil.saveFileAndReturnName(discoverDto.getFile()));
        discover.setCity(city);
        discoverRepository.save(discover);
    }

    public List<DiscoverDto> findAll(){
       List<Discover> discoverList =  discoverRepository.findAll();
       List<DiscoverDto> discoverDtoList =  new ArrayList<>();
       for(Discover discover : discoverList){
           DiscoverDto discoverDto = modelMapper.map(discover, DiscoverDto.class);
            discoverDtoList.add(discoverDto);
        }
        return discoverDtoList;
    }

    public DiscoverDto findById( Long id){
        Discover discover  = discoverRepository.findById(id).get();
        DiscoverDto discoverDto  = modelMapper.map(discover, DiscoverDto.class);
        return discoverDto;
    }

    public void update(DiscoverDto discoverDto) {

        Discover discoverInDb  = discoverRepository.findById(discoverDto.getId()).get();
        City city=null;
        if(discoverDto.getCityId()!=0)
            city=cityRepository.getOne(discoverDto.getCityId());

        discoverInDb.setCity(city);
        discoverInDb.setDetail(discoverDto.getDetail());
        discoverInDb.setTitle(discoverDto.getTitle());
        discoverInDb.setYoutube(discoverDto.getYoutube());

        if (discoverDto.getFile() != null) {
            fileStorageUtil.deleteFile(discoverInDb.getPhotoName());
            discoverInDb.setPhotoName(fileStorageUtil.saveFileAndReturnName(discoverDto.getFile()));
        }

        discoverRepository.save(discoverInDb);
    }

    public DiscoverDto findRandom() {
        List<Long> ids = discoverRepository.findIds();
        Integer count = ids.size();
        Random rnd  = new Random();
        Integer idIndex = rnd.nextInt(count);

      Discover discover=  discoverRepository.findById(ids.get(idIndex)).get();
      DiscoverDto discoverDto  = modelMapper.map(discover, DiscoverDto.class);
      return  discoverDto;

    }
}
