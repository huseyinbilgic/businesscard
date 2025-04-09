package com.algofusion.businesscard.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.errors.CustomException;
import com.algofusion.businesscard.mappers.UserMapper;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.responses.RegisterUserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.findByEmail(registerUserRequest.getEmail()).isPresent()) {
            throw new CustomException("Email already exists!");
        }
        if (userRepository.findByUsername(registerUserRequest.getUsername()).isPresent()) {
            throw new CustomException("Username already exists!");
        }

        User user = userMapper.toUser(registerUserRequest);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }
}
