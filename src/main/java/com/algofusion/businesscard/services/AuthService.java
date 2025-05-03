package com.algofusion.businesscard.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.requests.UserUpdateForRefreshTokenRequest;
import com.algofusion.businesscard.responses.JwtTokenResponse;
import com.algofusion.businesscard.services.security.JwtUtil;
import com.algofusion.businesscard.services.security.RedisTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;
    private final UserService userService;

    @Value("${refresh-token.expiration}")
    long expiration;

    public JwtTokenResponse login(LoginUserRequest loginUserRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getUsernameOrEmail(),
                        loginUserRequest.getPassword()));

        String token = generateTokenForUsername(loginUserRequest.getUsernameOrEmail());

        String refreshToken = jwtUtil.generateRefreshToken();

        userService.updateUserForRefreshToken(loginUserRequest.getUsernameOrEmail(),
                UserUpdateForRefreshTokenRequest.builder()
                        .refreshToken(refreshToken)
                        .refreshTokenExpiresAt(Instant.now().plus(expiration, ChronoUnit.DAYS))
                        .build());

        return JwtTokenResponse.builder()
                .jwtToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String token) {
        redisTokenService.deleteToken(token);
    }

    public String generateTokenForUsername(String username) {
        String token = jwtUtil.generateTokenForUserName(Map.of(), username);
        redisTokenService.storeToken(username, token);

        return token;
    }
}
