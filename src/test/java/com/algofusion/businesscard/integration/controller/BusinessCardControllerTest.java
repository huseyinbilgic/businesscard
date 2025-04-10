package com.algofusion.businesscard.integration.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import com.algofusion.businesscard.entities.Contact;
import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.ContactType;
import com.algofusion.businesscard.enums.PrivacyStatus;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.BusinessCardRepository;
import com.algofusion.businesscard.repositories.ContactRepository;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.BusinessCardRequest;
import com.algofusion.businesscard.requests.ContactRequest;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.responses.BusinessCardResponse;
import com.algofusion.businesscard.responses.ContactResponse;
import com.algofusion.businesscard.services.AuthService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BusinessCardControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private EntityManager entityManager;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private AuthService authService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BusinessCardRepository businessCardRepository;

        @Autowired
        private ContactRepository contactRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        User user1;
        BusinessCard businessCard1;
        Contact contact1;

        String token;

        String endpoint = "/api/business-cards/";

        @BeforeEach
        public void beforeEach() throws InterruptedException {
                user1 = User.builder()
                                .email("usermail@gmail.com")
                                .username("Username1")
                                .password(passwordEncoder.encode("user1234"))
                                .role(Role.CUSTOMER)
                                .build();

                userRepository.save(user1);

                businessCard1 = BusinessCard.builder()
                                .user(user1)
                                .bcCode(UUID.randomUUID().toString())
                                .fullName("User 1")
                                .company("Company 1")
                                .jobTitle("Computer Engineer")
                                .aboutIt("ABOUT")
                                .privacy(PrivacyStatus.PUBLIC)
                                .contacts(null)
                                .build();

                businessCardRepository.save(businessCard1);

                contact1 = Contact.builder()
                                .businessCard(businessCard1)
                                .contactType(ContactType.PHONE)
                                .label("Business phone")
                                .contactValue("0555 555 55 55")
                                .build();

                contactRepository.save(contact1);

                entityManager.refresh(businessCard1);

                takeToken(user1.getUsername(), "user1234");
        }

        @Test
        void testFetchAllBusinesCardsByUsername() throws Exception {
                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andReturn();
                String jsonResponse = result.getResponse().getContentAsString();

                List<BusinessCardResponse> responseList = objectMapper.readValue(
                                jsonResponse, new TypeReference<List<BusinessCardResponse>>() {
                                });

                assertEquals(1, responseList.size());

                BusinessCardResponse response = responseList.get(0);

                // assert the business card
                assertEquals(businessCard1.getId(), response.getId());
                assertEquals(businessCard1.getUser().getId(), response.getUserId());
                assertEquals(businessCard1.getBcCode(), response.getBcCode());
                assertEquals(businessCard1.getFullName(), response.getFullName());
                assertEquals(businessCard1.getCompany(), response.getCompany());
                assertEquals(businessCard1.getJobTitle(), response.getJobTitle());
                assertEquals(businessCard1.getAboutIt(), response.getAboutIt());
                assertEquals(businessCard1.getPrivacy(), response.getPrivacy());
                assertEquals(businessCard1.getCreatedAt(), response.getCreatedAt());
                assertEquals(businessCard1.getUpdatedAt(), response.getUpdatedAt());

                // assert the contacts
                assertEquals(1, response.getContacts().size());

                ContactResponse contactResponse = response.getContacts().get(0);
                assertEquals(contact1.getId(), contactResponse.getId());
                assertEquals(contact1.getBusinessCard().getId(), contactResponse.getBusinessCardId());
                assertEquals(contact1.getContactType(), contactResponse.getContactType());
                assertEquals(contact1.getLabel(), contactResponse.getLabel());
                assertEquals(contact1.getContactValue(), contactResponse.getContactValue());

        }

        @Test
        void testSaveNewBusinessCardByUsername() throws Exception {
                BusinessCardRequest businessCardRequest = createBusinessCardRequest(
                                "Username surname",
                                "I know A.S.",
                                "Assistant",
                                "I am a very excited man",
                                PrivacyStatus.PUBLIC,
                                List.of(
                                                createContactRequest(null, ContactType.PHONE, "Business Phone",
                                                                "53456345534"),
                                                createContactRequest(null, ContactType.ADDRESS, "Business Address",
                                                                "Business address 1")));

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(businessCardRequest)))
                                .andReturn();
                String jsonResponse = result.getResponse().getContentAsString();

                BusinessCardResponse response = objectMapper.readValue(
                                jsonResponse, new TypeReference<BusinessCardResponse>() {
                                });

                // assert the business card
                assertNotNull(response.getId());
                assertEquals(user1.getId(), response.getUserId());
                assertNotNull(response.getBcCode());
                assertEquals(businessCardRequest.getFullName(), response.getFullName());
                assertEquals(businessCardRequest.getCompany(), response.getCompany());
                assertEquals(businessCardRequest.getJobTitle(), response.getJobTitle());
                assertEquals(businessCardRequest.getAboutIt(), response.getAboutIt());
                assertEquals(businessCardRequest.getPrivacy(), response.getPrivacy());
                assertNotNull(response.getCreatedAt());
                assertNotNull(response.getUpdatedAt());

                // assert the contacts
                assertEquals(2, response.getContacts().size());

                ContactResponse contactR1 = response.getContacts().get(0);
                assertNotNull(contactR1.getId());
                assertEquals(response.getId(), contactR1.getBusinessCardId());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactType(),
                                contactR1.getContactType());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getLabel(), contactR1.getLabel());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactValue(),
                                contactR1.getContactValue());

                ContactResponse contactR2 = response.getContacts().get(1);
                assertNotNull(contactR2.getId());
                assertEquals(response.getId(), contactR2.getBusinessCardId());
                assertEquals(businessCardRequest.getContactsRequests().get(1).getContactType(),
                                contactR2.getContactType());
                assertEquals(businessCardRequest.getContactsRequests().get(1).getLabel(), contactR2.getLabel());
                assertEquals(businessCardRequest.getContactsRequests().get(1).getContactValue(),
                                contactR2.getContactValue());
        }

        @Test
        void testUpdateBusinessCard() throws Exception {
                BusinessCardRequest businessCardRequest = createBusinessCardRequest(
                                "New Username surname",
                                "New I know A.S.",
                                "New Assistant",
                                " New I am a very excited man",
                                PrivacyStatus.PRIVATE,
                                List.of(
                                                createContactRequest(contact1.getId(), ContactType.EMAIL, "New Email",
                                                                "user@gmail.com"),
                                                createContactRequest(null, ContactType.WEBSITE, "New Business website",
                                                                "website.com.tr")));

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch(endpoint + businessCard1.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(businessCardRequest)))
                                .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();

                BusinessCardResponse response = objectMapper.readValue(
                                jsonResponse, new TypeReference<BusinessCardResponse>() {
                                });

                // assert the business card
                assertEquals(businessCard1.getId(), response.getId());
                assertEquals(user1.getId(), response.getUserId());
                assertEquals(businessCard1.getBcCode(), response.getBcCode());
                assertEquals(businessCardRequest.getFullName(), response.getFullName());
                assertEquals(businessCardRequest.getCompany(), response.getCompany());
                assertEquals(businessCardRequest.getJobTitle(), response.getJobTitle());
                assertEquals(businessCardRequest.getAboutIt(), response.getAboutIt());
                assertEquals(businessCardRequest.getPrivacy(), response.getPrivacy());
                assertEquals(businessCard1.getCreatedAt(), response.getCreatedAt());

                // assert the contacts
                assertEquals(2, response.getContacts().size());

                ContactResponse contactR1 = response.getContacts().get(0);
                assertEquals(contact1.getId(), contactR1.getId());
                assertEquals(response.getId(), contactR1.getBusinessCardId());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getId(), contactR1.getId());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactType(),
                                contactR1.getContactType());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getLabel(), contactR1.getLabel());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactValue(),
                                contactR1.getContactValue());

                ContactResponse contactR2 = response.getContacts().get(1);
                assertNotNull(contactR2.getId());
                assertEquals(businessCard1.getId(), contactR2.getBusinessCardId());
                assertEquals(response.getId(), contactR2.getBusinessCardId());
                assertEquals(businessCardRequest.getContactsRequests().get(1).getContactType(),
                                contactR2.getContactType());
                assertEquals(businessCardRequest.getContactsRequests().get(1).getLabel(), contactR2.getLabel());
                assertEquals(businessCardRequest.getContactsRequests().get(1).getContactValue(),
                                contactR2.getContactValue());

        }

        @Test
        public void testDeleteBusinessCardById() throws Exception {
                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(endpoint + businessCard1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token))
                                .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();

                assertEquals("Business Card deleted " + businessCard1.getId(), jsonResponse);

                Optional<BusinessCard> byId = businessCardRepository.findById(businessCard1.getId());
                assertFalse(byId.isPresent());

                List<Contact> contacts = contactRepository.findAllByBusinessCardId(businessCard1.getId());
                assertEquals(0, contacts.size());
        }

        public BusinessCardRequest createBusinessCardRequest(String fullname, String company, String jobTitle,
                        String aboutIt, PrivacyStatus privacy, List<ContactRequest> contactsRequests) {
                return BusinessCardRequest.builder()
                                .fullName(fullname)
                                .company(company)
                                .jobTitle(jobTitle)
                                .aboutIt(aboutIt)
                                .privacy(privacy)
                                .contactsRequests(contactsRequests != null ? contactsRequests : Collections.emptyList())
                                .build();
        }

        public ContactRequest createContactRequest(Long id, ContactType contactType, String label,
                        String contactValue) {
                return ContactRequest.builder()
                                .id(id)
                                .contactType(contactType)
                                .label(label)
                                .contactValue(contactValue)
                                .build();
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
                contactRepository.deleteAll();
                businessCardRepository.deleteAll();
                userRepository.deleteAll();
        }
}
