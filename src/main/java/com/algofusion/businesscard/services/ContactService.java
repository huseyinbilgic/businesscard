package com.algofusion.businesscard.services;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.BusinessCard;
import com.algofusion.businesscard.entities.Contact;
import com.algofusion.businesscard.errors.CustomException;
import com.algofusion.businesscard.requests.ContactRequest;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {
    private final ObjectMapper objectMapper;

    public void updateContacts(BusinessCard businessCard, List<ContactRequest> contactRequests) {
        validateContactAuthorization(businessCard, contactRequests);

        Set<Long> existingIds = getExistingContactIds(businessCard);
        List<ContactRequest> validRequests = filterValidRequests(contactRequests, existingIds);
        Set<Long> validIds = extractValidContactIds(validRequests);

        removeDeletedContacts(businessCard, validIds);

        validRequests.forEach(contactR -> {
            if (contactR.getId() != null) {
                updateExistingContact(businessCard, contactR);
            } else {
                addNewContact(businessCard, contactR);
            }
        });
    }

    private void validateContactAuthorization(BusinessCard businessCard, List<ContactRequest> requests) {
        Set<Long> existingIds = getExistingContactIds(businessCard);

        Set<Long> unauthorizedIds = requests.stream()
                .map(ContactRequest::getId)
                .filter(Objects::nonNull)
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toSet());

        if (!unauthorizedIds.isEmpty()) {
            throw new CustomException("You do not have authorize to edit or delete a different contact");
        }
    }

    private Set<Long> getExistingContactIds(BusinessCard businessCard) {
        return businessCard.getContacts().stream()
                .map(Contact::getId)
                .collect(Collectors.toSet());
    }

    private List<ContactRequest> filterValidRequests(List<ContactRequest> requests, Set<Long> existingIds) {
        return requests.stream()
                .filter(r -> r.getId() == null || existingIds.contains(r.getId()))
                .collect(Collectors.toList());
    }

    private Set<Long> extractValidContactIds(List<ContactRequest> validRequests) {
        return validRequests.stream()
                .map(ContactRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void removeDeletedContacts(BusinessCard businessCard, Set<Long> validIds) {
        List<Contact> toRemove = businessCard.getContacts().stream()
                .filter(c -> !validIds.contains(c.getId()))
                .collect(Collectors.toList());

        businessCard.getContacts().removeAll(toRemove);
    }

    private void updateExistingContact(BusinessCard businessCard, ContactRequest request) {
        Contact contact = businessCard.getContacts().stream()
                .filter(c -> c.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException("Contact not found: " + request.getId()));

        applyUpdates(contact, request);
    }

    private void addNewContact(BusinessCard businessCard, ContactRequest request) {
        Contact newContact = Contact.builder()
                .businessCard(businessCard)
                .build();

        applyUpdates(newContact, request);
        businessCard.getContacts().add(newContact);
    }

    private void applyUpdates(Contact contact, ContactRequest request) {
        try {
            objectMapper.updateValue(contact, request);
        } catch (JsonMappingException e) {
            throw new CustomException("Error updating contact: " + request.getId() + e.getMessage());
        }
    }
}
