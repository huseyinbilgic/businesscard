package com.algofusion.businesscard.integration.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    User user1;

    String endpoint = "/api/auth/";

    @BeforeEach
    public void beforeEach() {
        user1 = User.builder()
                .email("usermail@gmail.com")
                .username("Username 1")
                .password(passwordEncoder.encode("user1234"))
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user1);
    }

    @Test
    void testLogin() throws Exception {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder().usernameOrEmail("Username 1")
                .password("user1234").build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        assertNotNull(jsonResponse);
        // .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
