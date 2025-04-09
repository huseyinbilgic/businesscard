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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.responses.RegisterUserResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(endpoint + "signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        RegisterUserResponse response = objectMapper.readValue(
                jsonResponse, new TypeReference<RegisterUserResponse>() {
                });

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(registerUserRequest.getEmail(), response.getEmail());
        assertEquals(registerUserRequest.getUsername(), response.getUsername());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        // .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(registerUserRequest.getEmail()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(registerUserRequest.getUsername()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(Role.CUSTOMER.toString()))
        // .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty())
        // .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
