package com.algofusion.businesscard.integration.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    String endpoint = "/api/user/";

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    void registerUser_WithValidRequest_ShouldReturnCreated() throws Exception {
        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .email("usermail@gmail.com")
                .username("Username 1")
                .password("user1234")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerUserRequest.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(registerUserRequest.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(Role.CUSTOMER.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
