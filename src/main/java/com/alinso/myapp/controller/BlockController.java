package com.alinso.myapp.controller;

import com.alinso.myapp.dto.user.ProfileDto;
import com.alinso.myapp.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("block")
public class BlockController {


    @Autowired
    BlockService blockService;

    @GetMapping("block/{blockedId}")
    public ResponseEntity<?> follow(@PathVariable("blockedId") Long blockedId){
        Boolean result = blockService.block(blockedId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("isBlocked/{oppositId}")
    public ResponseEntity<?> isFollowing(@PathVariable("oppositId") Long oppositId){
        Boolean result = blockService.isThereABlock(oppositId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("myBlocks")
    public ResponseEntity<?> myBlocks(){

        List<ProfileDto> profileDtoList = blockService.findMyBlocks();
        return new ResponseEntity<>(profileDtoList,HttpStatus.OK);
    }

}
