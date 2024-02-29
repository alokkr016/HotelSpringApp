package com.cn.hotel.service;

import com.cn.hotel.dto.JwtRequest;
import com.cn.hotel.dto.JwtResponse;
import com.cn.hotel.jwt.JwtAuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager manager;

    @Autowired
    JwtAuthenticationHelper jwtAuthenticationHelper;

    @Autowired
    UserDetailsService userDetailsService;

    public JwtResponse login(JwtRequest jwtRequest) {
         this.doAuthenticate(jwtRequest.getUsername(), jwtRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token =  jwtAuthenticationHelper.generateToken(userDetails);
        JwtResponse jwtResponse = JwtResponse.builder().jwtToken(token).build();
        return jwtResponse;
    }

    private void doAuthenticate(String username,String password){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        try {
            manager.authenticate(usernamePasswordAuthenticationToken);
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
