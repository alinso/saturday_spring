package com.alinso.myapp.service;

import com.alinso.myapp.dto.ChangePasswordDto;
import com.alinso.myapp.dto.PhotoDto;
import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserNotFoundException;
import com.alinso.myapp.file.FileStorageService;
import com.alinso.myapp.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    FileStorageService fileStorageService;

    @Value("${upload.profile.path}")
    private String profilPicUploadPath;

    public User register(User newUser) {
            newUser.setEmail(newUser.getEmail());
            newUser.setConfirmPassword("");
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            return userRepository.save(newUser);
    }


    public UserDto update(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        User userInDb;
        try {
            userInDb = userRepository.findById(user.getId()).get();
        } catch (Exception e) {
            throw new UserNotFoundException("user not found id : " + userDto.getId());
        }
        user.setPassword(userInDb.getPassword());
        user.setProfilePicName(userInDb.getProfilePicName());
        userRepository.save(user);
        return userDto;
    }

    public UserDto findById(Long id) {
        try {
            User user = userRepository.findById(id).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);

            userDto.setProfilePicUrl(user.getProfilePicName());
            return userDto;
        } catch (Exception e) {
            throw new UserNotFoundException("user not found id : " + id);
        }
    }

    public UserDto findByPhone(Integer phone) {
        try {
            User user = userRepository.findByPhone(phone).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return userDto;
        } catch (Exception e) {
            return null;
        }
    }

    public UserDto findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return userDto;
        } catch (Exception e) {
            return null;
        }
    }


    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        }catch (Exception e){
            throw new UserNotFoundException("user not found id : " + id);
        }
    }

    public String updateProfilePic(PhotoDto photoDto){

       String extension =  FilenameUtils.getExtension(photoDto.getFile().getOriginalFilename());
       String newName  = fileStorageService.makeFileName()+"."+extension;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //save new file and remove old one
        fileStorageService.deleteFile(profilPicUploadPath+user.getProfilePicName());
        fileStorageService.storeFile(photoDto.getFile(), profilPicUploadPath,newName);

        //update database
        user.setProfilePicName(newName);
        userRepository.save(user);
        return newName;
    }


    public Boolean changePassword(ChangePasswordDto changePasswordDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));

        userRepository.save(user);

        return true;
    }
}
