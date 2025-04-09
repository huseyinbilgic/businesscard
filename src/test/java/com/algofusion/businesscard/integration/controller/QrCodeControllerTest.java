package com.algofusion.businesscard.integration.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.BusinessCard;
import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.PrivacyStatus;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.BusinessCardRepository;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.services.AuthService;

import jakarta.persistence.EntityManager;

@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QrCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessCardRepository businessCardRepository;

    User user1;
    BusinessCard businessCard1;
    String token;

    String endpoint = "/api/qr-code/";

    @BeforeEach
    public void beforeEach() throws InterruptedException {
        user1 = User.builder()
                .email("usermail@gmail.com")
                .username("Username 1")
                .password(passwordEncoder.encode("user1234"))
                .role(Role.CUSTOMER)
                .build();

        entityManager.persist(user1);
        entityManager.flush();
        entityManager.refresh(user1);

        businessCard1 = BusinessCard.builder()
                .user(user1)
                .bcCode(UUID.randomUUID().toString())
                .fullName("User 1")
                .company("Company 1")
                .jobTitle("Computer Engineer")
                .aboutIt("ABOUT")
                .privacy(PrivacyStatus.PUBLIC)
                .build();

        entityManager.persist(businessCard1);
        entityManager.flush();
        entityManager.refresh(businessCard1);

        takeToken(user1.getUsername(), "user1234");
    }

    @Test
    public void testGenerateQrCodeForBusinessCard() throws Exception {
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get(endpoint + "generate?text=" + businessCard1.getBcCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.length() != 0);
    }

    public void takeToken(String username, String password) {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .usernameOrEmail(username)
                .password(password)
                .build();

        token = authService.login(loginUserRequest);
    }

    @AfterEach
    public void afterEach() {
        businessCardRepository.deleteAll();
        userRepository.deleteAll();
    }
}
