package com.alinso.myapp.service;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.entity.Block;
import com.alinso.myapp.entity.Follow;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.repository.BlockRepository;
import com.alinso.myapp.repository.FollowRepository;
import com.alinso.myapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockService {


    @Autowired
    BlockRepository blockRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    public Boolean block(Long blockedId){

        User blocker  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User blocked =userRepository.findById(blockedId).get();
        Block block = blockRepository.findBlockByBlockedAndBlocker(blocked,blocker);

        Boolean isBlocked;
        if(block==null){
            Block newBlock = new Block();
            newBlock.setBlocker(blocker);
            newBlock.setBlocked(blocked);
            blockRepository.save(newBlock);
            isBlocked=true;
        }else{
            blockRepository.delete(block);
            isBlocked=false;
        }
        return isBlocked;
    }

    private Boolean isBlockedByIt(Long blockerId) {

        User blocked  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User blocker =userRepository.findById(blockerId).get();

        Block block = blockRepository.findBlockByBlockedAndBlocker(blocked,blocker);
        if(block==null)
            return false;
        else
            return true;
    }


    private Boolean isBlockedIt(Long blockedId) {

        User blocker  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User blocked =userRepository.findById(blockedId).get();

        Block block = blockRepository.findBlockByBlockedAndBlocker(blocked,blocker);
        if(block==null)
            return false;
        else
            return true;
    }

    public Boolean isThereABlock(Long oppositId){
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")){ //if user not logged in cannot control the blockage status
            return false;
        }
        return (isBlockedIt(oppositId) || isBlockedByIt(oppositId));
    }




    public List<ProfileDto> findMyBlocks() {
        User blocker  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> blockeds = blockRepository.findUsersBlcokedByTheUser(blocker);

        List<ProfileDto> profileDtos  = new ArrayList<>();
        for(User user: blockeds){
            ProfileDto profileDto = modelMapper.map(user,ProfileDto.class);
            profileDtos.add(profileDto);
        }

        return profileDtos;
    }


}
