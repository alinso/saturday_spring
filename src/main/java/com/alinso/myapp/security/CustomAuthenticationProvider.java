package com.alinso.myapp.security;

import com.alinso.myapp.exception.UserWarningException;
import com.alinso.myapp.service.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
@Component
public class CustomAuthenticationProvider
        implements AuthenticationProvider {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();


        UserDetails userDetails = customUserDetailsService.loadUserByUsername(name);

        if(!userDetails.isEnabled()){
            throw new UserWarningException("Buraya tıklayıp telefonuna gelen kodu girmelisin");
        }


        if ( BCrypt.checkpw(password, userDetails.getPassword())) {

            // use the credentials
            // and authenticate against the third-party system
            return new UsernamePasswordAuthenticationToken(
                    userDetails, password, new ArrayList<>());
        } else {
            throw new UserWarningException("Kullanıcı adı veya şifre yanlış");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
