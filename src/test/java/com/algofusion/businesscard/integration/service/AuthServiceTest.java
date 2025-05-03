package com.algofusion.businesscard.integration.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.services.AuthService;
import com.algofusion.businesscard.services.security.JwtUtil;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    User user1;

    @BeforeEach
    public void beforeEach() {
        user1 = User.builder()
                .email("usermail@gmail.com")
                .username("Username1")
                .password(passwordEncoder.encode("user1234"))
                .role(Role.CUSTOMER)
                .refreshToken(jwtUtil.generateRefreshToken())
                .refreshTokenExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        userRepository.save(user1);
    }

    @Test
    void testLogin_WithValidParameters_ShouldBeLogin() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder().usernameOrEmail("Username1")
                .password("user1234").build();
        String login = authService.login(loginUserRequest).getJwtToken();

        assertNotNull(login);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
