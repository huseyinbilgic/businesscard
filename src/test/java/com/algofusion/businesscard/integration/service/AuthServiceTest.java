package com.algofusion.businesscard.integration.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.services.AuthService;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    User user1;

    @BeforeEach
    public void beforeEach() {
        user1 = User.builder()
                .email("usermail@gmail.com")
                .username("Username1")
                .password("$2a$12$/NGOUpCcOmg1mCCyVfjvi.66ew/YLcHRpA7Wq/N8pO92RanPrs/T2") // user1234
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user1);
    }

    @Test
    void testLogin_WithValidParameters_ShouldBeLogin() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder().usernameOrEmail("Username1")
                .password("user1234").build();
        String login = authService.login(loginUserRequest);

        assertNotNull(login);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
