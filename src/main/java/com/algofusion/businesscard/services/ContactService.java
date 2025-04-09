package com.algofusion.businesscard.services;

import java.util.List;

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
        contactRequests.forEach(contactR -> {
            if (contactR.getId() != null) {
                updateExistingContact(businessCard, contactR);
            } else {
                addNewContact(businessCard, contactR);
            }
        });
    }

    private void updateExistingContact(BusinessCard businessCard, ContactRequest contactR) {
        Contact contact = businessCard.getContacts().stream()
                .filter(c -> c.getId().equals(contactR.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException("Contact not found: " + contactR.getId()));

        applyUpdates(contact, contactR);
    }

    private void addNewContact(BusinessCard businessCard, ContactRequest contactR) {
        Contact newContact = Contact.builder().businessCard(businessCard).build();
        applyUpdates(newContact, contactR);
        businessCard.getContacts().add(newContact);
    }

    private void applyUpdates(Contact contact, ContactRequest contactR) {
        try {
            objectMapper.updateValue(contact, contactR);
        } catch (JsonMappingException e) {
            throw new CustomException("Error updating contact: " + contactR.getId());
        }
    }
}
