package com.alinso.myapp.service.security;

import com.alinso.myapp.entity.User;
import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(username).get();
            return user;
        } catch (Exception e) {
            throw new UserWarningException("Kullanıcı adı veya şifre yanlış");
        }
    }


    public User loadUserById(long id) {
        User user = userRepository.findById(id).get();
        return user;
    }
}