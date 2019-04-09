package com.alinso.myapp.service;

import com.alinso.myapp.entity.Block;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.entity.dto.user.ProfileDto;
import com.alinso.myapp.repository.BlockRepository;
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
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    public Boolean block(Long blockedId){

        User blocker  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User blocked =userService.findEntityById(blockedId);
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
        User blocker =userService.findEntityById(blockerId);

        Block block = blockRepository.findBlockByBlockedAndBlocker(blocked,blocker);
        if(block==null)
            return false;
        else
            return true;
    }


    private Boolean isBlockedIt(Long blockedId) {

        User blocker  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User blocked =userService.findEntityById(blockedId);

        Block block = blockRepository.findBlockByBlockedAndBlocker(blocked,blocker);
        if(block==null)
            return false;
        else
            return true;
    }

    public Boolean isThereABlock(Long oppositId){
        if(SecurityContextHolder.getContext().getAuthentication()==null){
            //and this means that event triggered by the system(ex: new review available )
            return false;
        }
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")){
            //and this means that event triggered by the system(ex: register )
            return false;
        }
        return (isBlockedIt(oppositId) || isBlockedByIt(oppositId));
    }




    public List<ProfileDto> findMyBlocks() {
        User blocker  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> blockeds = blockRepository.findUsersBlcokedByTheUser(blocker);

        List<ProfileDto> profileDtos  = new ArrayList<>();
        for(User user: blockeds){
            profileDtos.add(userService.toProfileDto(user));
        }

        return profileDtos;
    }


}
