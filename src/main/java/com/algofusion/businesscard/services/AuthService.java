package com.algofusion.businesscard.services;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.services.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public String login(LoginUserRequest loginUserRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getUsernameOrEmail(),
                        loginUserRequest.getPassword()));
        return jwtUtil.generateTokenForUserName(Map.of(), loginUserRequest.getUsernameOrEmail());
    }
    
}
