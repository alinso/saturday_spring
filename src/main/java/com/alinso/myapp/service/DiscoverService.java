package com.alinso.myapp.service;

import com.alinso.myapp.entity.City;
import com.alinso.myapp.entity.Announcement;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.discover.DiscoverDto;
import com.alinso.myapp.repository.CityRepository;
import com.alinso.myapp.repository.DiscoverRepository;
import com.alinso.myapp.util.FileStorageUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        Announcement announcement = modelMapper.map(discoverDto, Announcement.class);
        announcement.setPhotoName(fileStorageUtil.saveFileAndReturnName(discoverDto.getFile()));
        announcement.setCity(city);
        discoverRepository.save(announcement);
    }

    public List<DiscoverDto> findAll(){
       List<Announcement> announcementList =  discoverRepository.findAll();
       List<DiscoverDto> discoverDtoList =  new ArrayList<>();
       for(Announcement announcement : announcementList){
           DiscoverDto discoverDto = modelMapper.map(announcement, DiscoverDto.class);
            discoverDtoList.add(discoverDto);
        }
        return discoverDtoList;
    }

    public DiscoverDto findById( Long id){
        Announcement announcement = discoverRepository.findById(id).get();
        DiscoverDto discoverDto  = modelMapper.map(announcement, DiscoverDto.class);
        return discoverDto;
    }

    public void update(DiscoverDto discoverDto) {

        Announcement announcementInDb = discoverRepository.findById(discoverDto.getId()).get();
        City city=null;
        if(discoverDto.getCityId()!=0)
            city=cityRepository.getOne(discoverDto.getCityId());

        announcementInDb.setCity(city);
        announcementInDb.setDetail(discoverDto.getDetail());
        announcementInDb.setTitle(discoverDto.getTitle());
        announcementInDb.setYoutube(discoverDto.getYoutube());

        if (discoverDto.getFile() != null) {
            fileStorageUtil.deleteFile(announcementInDb.getPhotoName());
            announcementInDb.setPhotoName(fileStorageUtil.saveFileAndReturnName(discoverDto.getFile()));
        }

        discoverRepository.save(announcementInDb);
    }

    public DiscoverDto findRandom() {
        List<Long> ids = discoverRepository.findIds();
        Integer count = ids.size();
        Random rnd  = new Random();
        Integer idIndex = rnd.nextInt(count);

      Announcement announcement =  discoverRepository.findById(ids.get(idIndex)).get();
      DiscoverDto discoverDto  = modelMapper.map(announcement, DiscoverDto.class);
      User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if(user.getPoint()<20 && discoverDto.getId()==84)
      {
          discoverDto=null;
      }

      return  discoverDto;

    }
}
