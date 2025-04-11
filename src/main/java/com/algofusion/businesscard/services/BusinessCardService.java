package com.algofusion.businesscard.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.BusinessCard;
import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.errors.CustomException;
import com.algofusion.businesscard.helper.PermissionHelper;
import com.algofusion.businesscard.mappers.BusinessCardMapper;
import com.algofusion.businesscard.repositories.BusinessCardRepository;
import com.algofusion.businesscard.requests.BusinessCardRequest;
import com.algofusion.businesscard.responses.BusinessCardResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCardService {
    private final BusinessCardRepository businessCardRepository;
    private final UserService userService;
    private final BusinessCardMapper businessCardMapper;
    private final ObjectMapper objectMapper;
    private final ContactService contactService;
    private final PermissionHelper permissionHelper;

    public List<BusinessCardResponse> fetchAllBusinesCardsByUsername(String username) {
        return businessCardRepository.findAllByUserUsername(username)
                .stream()
                .map(businessCardMapper::toBusinessCardResponse)
                .toList();
    }

    public BusinessCardResponse saveNewBusinessCardByUsername(String username,
            BusinessCardRequest businessCardRequest) {
        User byUsername = userService.findByUsername(username);

        BusinessCard businessCard = businessCardMapper.toBusinessCard(businessCardRequest);
        businessCard.setUser(byUsername);
        businessCard.setBcCode(UUID.randomUUID().toString());
        businessCard.getContacts().forEach((contact) -> contact.setBusinessCard(businessCard));
        businessCardRepository.save(businessCard);
        return businessCardMapper.toBusinessCardResponse(businessCard);
    }

    public BusinessCardResponse updateBusinessCard(String username, Long businessCardId,
            BusinessCardRequest businessCardRequest) {
        BusinessCard byId = fetchBusinessCardById(businessCardId);

        permissionHelper.checkUsername(byId.getUser().getUsername(), username);
        try {
            objectMapper.updateValue(byId, businessCardRequest);
            contactService.updateContacts(byId, businessCardRequest.getContactsRequests());
            businessCardRepository.save(byId);
        } catch (JsonMappingException e) {
            throw new CustomException("Failed to map BusinessCardRequest to BusinessCard" + e);
        }

        return businessCardMapper.toBusinessCardResponse(byId);
    }

    public String deleteBusinessCardById(String username, Long id) {
        BusinessCard byId = fetchBusinessCardById(id);

        permissionHelper.checkUsername(byId.getUser().getUsername(), username);
        businessCardRepository.delete(byId);
        return "Business Card deleted " + id;
    }

    private BusinessCard fetchBusinessCardById(Long id) {
        return businessCardRepository.findById(id)
                .orElseThrow(() -> new CustomException("Business Card not found" + id));
    }
}
