package com.alinso.myapp.service;

import com.alinso.myapp.dto.UserDto;
import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserNotFoundException;
import com.alinso.myapp.exception.UsernameAlreadyExistsException;
import com.alinso.myapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User register(User newUser) {
        try {
//            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            //TODO: password will be hashed

            newUser.setEmail(newUser.getEmail());
            newUser.setConfirmPassword("");
            return userRepository.save(newUser);

        } catch (Exception e) {
            throw new UsernameAlreadyExistsException("Username '" + newUser.getEmail() + "' already exists");
        }
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
        userRepository.save(user);
        return userDto;
    }

    public UserDto findById(Long id) {
        try {
            User user = userRepository.findById(id).get();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return userDto;
        } catch (Exception e) {
            throw new UserNotFoundException("user not found id : " + id);
        }
    }


    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        }catch (Exception e){
            throw new UserNotFoundException("user not found id : " + id);
        }
    }


}
