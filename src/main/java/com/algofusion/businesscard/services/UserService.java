package com.algofusion.businesscard.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.dto.PrivacyUser;
import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.errors.CustomException;
import com.algofusion.businesscard.mappers.UserMapper;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.requests.UserUpdateForRefreshTokenRequest;
import com.algofusion.businesscard.responses.RegisterUserResponse;
import com.algofusion.businesscard.services.security.JwtUtil;
import com.algofusion.businesscard.specifications.UserSpecifications;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Value("${refresh-token.expiration}")
    long expiration;

    public RegisterUserResponse saveUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.getEmail())) {
            throw new CustomException("Email already exists!");
        }
        if (userRepository.existsByUsername(registerUserRequest.getUsername())) {
            throw new CustomException("Username already exists!");
        }

        User user = userMapper.toUser(registerUserRequest);
        user.setRole(Role.CUSTOMER);
        user.setRefreshToken(jwtUtil.generateRefreshToken());
        user.setRefreshTokenExpiresAt(Instant.now().plus(expiration, ChronoUnit.DAYS));
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found: " + username));
    }

    public User findByUsernameOrEmail(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new CustomException("User not found: " + username));
    }

    public RegisterUserResponse updateUserForRefreshToken(String username,
            UserUpdateForRefreshTokenRequest userUpdateForRefreshTokenRequest) {
        User byUsernameOrEmail = findByUsernameOrEmail(username);

        try {
            objectMapper.updateValue(byUsernameOrEmail, userUpdateForRefreshTokenRequest);
            userRepository.save(byUsernameOrEmail);
        } catch (JsonMappingException e) {
            throw new CustomException("Failed to map UserUpdateForRefreshTokenRequest to User" + e);
        }

        return userMapper.toUserResponse(byUsernameOrEmail);
    }

    public List<PrivacyUser> searchUsers(String keyword) {
        Specification<User> spec = Specification.where(UserSpecifications.nameContains(keyword));
        List<User> all = userRepository.findAll(spec);
        return all.stream().map(u -> userMapper.toPrivacyUser(u)).toList();
    }
}
