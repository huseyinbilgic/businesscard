package com.algofusion.businesscard.integration.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.responses.RegisterUserResponse;
import com.algofusion.businesscard.services.UserService;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    void registerUser_WithValidRequest_ShouldBeCreated() {
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .email("usermail@gmail.com")
                .username("Username1")
                .password(passwordEncoder.encode("user1234"))
                .build();

        RegisterUserResponse registerUser = userService.saveUser(registerUserRequest);

        assertNotNull(registerUser);
        assertNotNull(registerUser.getId());
        assertNotNull(registerUser.getCreatedAt());
        assertNotNull(registerUser.getUpdatedAt());

        assertEquals(registerUserRequest.getEmail(), registerUser.getEmail());
        assertEquals(registerUserRequest.getUsername(), registerUser.getUsername());
        assertEquals(Role.CUSTOMER, registerUser.getRole());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
