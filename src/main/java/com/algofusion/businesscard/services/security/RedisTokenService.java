package com.algofusion.businesscard.services.security;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    private final JwtUtil jwtUtil;

    public void storeToken(String username, String token) {
        redisTemplate.opsForValue().set(token, username, jwtUtil.expiration, TimeUnit.MILLISECONDS);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }

    public boolean isTokenValid(String token) {
        return redisTemplate.opsForValue().get(token) != null;
    }
}
