package com.algofusion.businesscard.integration.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
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
import com.algofusion.businesscard.enums.ContactType;
import com.algofusion.businesscard.enums.PrivacyStatus;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.BusinessCardRepository;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.requests.BusinessCardRequest;
import com.algofusion.businesscard.requests.ContactRequest;
import com.algofusion.businesscard.responses.BusinessCardResponse;
import com.algofusion.businesscard.responses.ContactResponse;
import com.algofusion.businesscard.services.BusinessCardService;
import com.algofusion.businesscard.services.security.JwtUtil;

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

        @Autowired
        private JwtUtil jwtUtil;

        User user1;
        BusinessCard businessCard1;

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

                businessCard1 = BusinessCard.builder()
                                .user(user1)
                                .bcCode(UUID.randomUUID().toString())
                                .fullName("User 1")
                                .company("Company 1")
                                .jobTitle("Computer Engineer")
                                .aboutIt("ABOUT")
                                .privacy(PrivacyStatus.PUBLIC)
                                .build();
                businessCardRepository.save(businessCard1);
        }

        @Test
        void testDeleteBusinessCardById() {
                String response = businessCardService.deleteBusinessCardById(user1.getUsername(),
                                businessCard1.getId());

                assertEquals("Business Card deleted " + businessCard1.getId(), response);

                Optional<BusinessCard> byId = businessCardRepository.findById(businessCard1.getId());
                assertFalse(byId.isPresent());
        }

        @Test
        void testFetchAllBusinesCardsByUsername() {
                BusinessCard businessCard2 = BusinessCard.builder()
                                .user(user1)
                                .bcCode(UUID.randomUUID().toString())
                                .fullName("User 2")
                                .company("Company 2")
                                .jobTitle("Software Engineer")
                                .aboutIt("ABOUT")
                                .privacy(PrivacyStatus.PUBLIC)
                                .build();
                businessCardRepository.save(businessCard2);

                List<BusinessCardResponse> cards = businessCardService
                                .fetchAllBusinesCardsByUsername(user1.getUsername());

                assertEquals(2, cards.size());

                Optional<BusinessCardResponse> card1 = cards.stream()
                                .filter((bc) -> bc.getId() == businessCard1.getId())
                                .findFirst();

                assertNotNull(card1);
                assertEquals(businessCard1.getAboutIt(), card1.get().getAboutIt());
                assertEquals(businessCard1.getBcCode(), card1.get().getBcCode());
                assertEquals(businessCard1.getCompany(), card1.get().getCompany());
                assertEquals(businessCard1.getCreatedAt(), card1.get().getCreatedAt());
                assertEquals(businessCard1.getFullName(), card1.get().getFullName());
                assertEquals(businessCard1.getJobTitle(), card1.get().getJobTitle());
                assertEquals(businessCard1.getPrivacy(), card1.get().getPrivacy());
                assertEquals(businessCard1.getUpdatedAt(), card1.get().getUpdatedAt());
                assertEquals(businessCard1.getUser().getId(), card1.get().getUserId());
                assertEquals(businessCard1.getContacts().size(), card1.get().getContacts().size());

                Optional<BusinessCardResponse> card2 = cards.stream()
                                .filter((bc) -> bc.getId() == businessCard2.getId())
                                .findFirst();

                assertNotNull(card2);
                assertEquals(businessCard2.getAboutIt(), card2.get().getAboutIt());
                assertEquals(businessCard2.getBcCode(), card2.get().getBcCode());
                assertEquals(businessCard2.getCompany(), card2.get().getCompany());
                assertEquals(businessCard2.getCreatedAt(), card2.get().getCreatedAt());
                assertEquals(businessCard2.getFullName(), card2.get().getFullName());
                assertEquals(businessCard2.getJobTitle(), card2.get().getJobTitle());
                assertEquals(businessCard2.getPrivacy(), card2.get().getPrivacy());
                assertEquals(businessCard2.getUpdatedAt(), card2.get().getUpdatedAt());
                assertEquals(businessCard2.getUser().getId(), card2.get().getUserId());
                assertEquals(businessCard2.getContacts().size(), card2.get().getContacts().size());
        }

        @Test
        void testSaveNewBusinessCardByUsername() {
                BusinessCardRequest businessCardRequest = createBusinessCardRequest(
                                "Username surname",
                                "I know A.S.",
                                "Assistant",
                                "I am a very excited man",
                                PrivacyStatus.PUBLIC,
                                List.of(
                                                createContactRequest(null, ContactType.PHONE, "Business Phone",
                                                                "53456345534")));
                BusinessCardResponse saveNewBusinessCardByUsername = businessCardService
                                .saveNewBusinessCardByUsername(user1.getUsername(), businessCardRequest);

                assertNotNull(saveNewBusinessCardByUsername.getId());
                assertEquals(user1.getId(), saveNewBusinessCardByUsername.getUserId());
                assertNotNull(saveNewBusinessCardByUsername.getBcCode());
                assertEquals(businessCardRequest.getFullName(), saveNewBusinessCardByUsername.getFullName());
                assertEquals(businessCardRequest.getCompany(), saveNewBusinessCardByUsername.getCompany());
                assertEquals(businessCardRequest.getJobTitle(), saveNewBusinessCardByUsername.getJobTitle());
                assertEquals(businessCardRequest.getAboutIt(), saveNewBusinessCardByUsername.getAboutIt());
                assertEquals(businessCardRequest.getPrivacy(), saveNewBusinessCardByUsername.getPrivacy());
                assertNotNull(saveNewBusinessCardByUsername.getCreatedAt());
                assertNotNull(saveNewBusinessCardByUsername.getUpdatedAt());

                // assert the contacts
                assertEquals(1, saveNewBusinessCardByUsername.getContacts().size());

                ContactResponse contactR1 = saveNewBusinessCardByUsername.getContacts().get(0);
                assertNotNull(contactR1.getId());
                assertEquals(saveNewBusinessCardByUsername.getId(), contactR1.getBusinessCardId());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactType(),
                                contactR1.getContactType());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getLabel(), contactR1.getLabel());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactValue(),
                                contactR1.getContactValue());
        }

        @Test
        void testUpdateBusinessCard() {
                BusinessCardRequest businessCardRequest = createBusinessCardRequest(
                                "New Username surname",
                                "New I know A.S.",
                                "New Assistant",
                                " New I am a very excited man",
                                PrivacyStatus.PRIVATE,
                                List.of(
                                                createContactRequest(null, ContactType.WEBSITE, "New Business website",
                                                                "website.com.tr")));

                BusinessCardResponse updateBusinessCard = businessCardService.updateBusinessCard(user1.getUsername(),
                                businessCard1.getId(),
                                businessCardRequest);

                // assert the business card
                assertEquals(businessCard1.getId(), updateBusinessCard.getId());
                assertEquals(user1.getId(), updateBusinessCard.getUserId());
                assertEquals(businessCard1.getBcCode(), updateBusinessCard.getBcCode());
                assertEquals(businessCardRequest.getFullName(), updateBusinessCard.getFullName());
                assertEquals(businessCardRequest.getCompany(), updateBusinessCard.getCompany());
                assertEquals(businessCardRequest.getJobTitle(), updateBusinessCard.getJobTitle());
                assertEquals(businessCardRequest.getAboutIt(), updateBusinessCard.getAboutIt());
                assertEquals(businessCardRequest.getPrivacy(), updateBusinessCard.getPrivacy());
                assertEquals(businessCard1.getCreatedAt(), updateBusinessCard.getCreatedAt());

                // assert the contacts
                assertEquals(1, updateBusinessCard.getContacts().size());

                ContactResponse contactR = updateBusinessCard.getContacts().get(0);
                assertNotNull(contactR.getId());
                assertEquals(businessCard1.getId(), contactR.getBusinessCardId());
                assertEquals(updateBusinessCard.getId(), contactR.getBusinessCardId());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactType(),
                                contactR.getContactType());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getLabel(), contactR.getLabel());
                assertEquals(businessCardRequest.getContactsRequests().get(0).getContactValue(),
                                contactR.getContactValue());
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

        @AfterEach
        public void afterEach() {
                businessCardRepository.deleteAll();
                userRepository.deleteAll();
        }
}
