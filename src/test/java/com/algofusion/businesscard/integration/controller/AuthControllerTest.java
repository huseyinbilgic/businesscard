package com.algofusion.businesscard.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.services.security.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    User user1;

    String endpoint = "/api/auth/";

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
    void registerUser_WithValidRequest_ShouldReturnCreated() throws Exception {
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .email("usermail2@gmail.com")
                .username("Username2")
                .password("user1234")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        String response = result.getResponse().getContentAsString();

        assertNotNull(response);
        assertEquals("Registration successful", response);


        // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        // .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerUserRequest.getEmail()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(registerUserRequest.getUsername()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(Role.CUSTOMER.toString()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty())
        // .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void testLogin() throws Exception {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder().usernameOrEmail("Username1")
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
