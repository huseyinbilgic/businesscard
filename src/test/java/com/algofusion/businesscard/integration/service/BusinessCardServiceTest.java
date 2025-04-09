package com.algofusion.businesscard.integration.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.BusinessCard;
import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.PrivacyStatus;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.BusinessCardRepository;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.services.BusinessCardService;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BusinessCardServiceTest {

    @Autowired
    private BusinessCardService businessCardService;

    @Autowired
    private BusinessCardRepository businessCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    User user1;

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
    void testDeleteBusinessCardById() {
        BusinessCard businessCard1 = BusinessCard.builder()
                .user(user1)
                .bcCode(UUID.randomUUID().toString())
                .fullName("User 1")
                .company("Company 1")
                .jobTitle("Computer Engineer")
                .aboutIt("ABOUT")
                .privacy(PrivacyStatus.PUBLIC)
                .build();
        businessCardRepository.save(businessCard1);

        String response = businessCardService.deleteBusinessCardById(user1.getUsername(), businessCard1.getId());

        assertEquals("Business Card deleted " + businessCard1.getId(), response);

        Optional<BusinessCard> byId = businessCardRepository.findById(businessCard1.getId());
        assertFalse(byId.isPresent());

    }

    @Test
    void testFetchAllBusinesCardsByUsername() {

    }

    @Test
    void testSaveNewBusinessCardByUsername() {

    }

    @Test
    void testUpdateBusinessCard() {

    }

    @AfterEach
    public void afterEach() {
        businessCardRepository.deleteAll();
        userRepository.deleteAll();
    }
}
