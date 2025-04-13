package com.algofusion.businesscard.services;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.services.security.JwtUtil;
import com.algofusion.businesscard.services.security.RedisTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;

    public String login(LoginUserRequest loginUserRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getUsernameOrEmail(),
                        loginUserRequest.getPassword()));
        String token = jwtUtil.generateTokenForUserName(Map.of(), loginUserRequest.getUsernameOrEmail());
        redisTokenService.storeToken(loginUserRequest.getUsernameOrEmail(), token);
        return token;
    }

    public void logout(String token){
        redisTokenService.deleteToken(token);
    }

}
