package com.algofusion.businesscard.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.algofusion.businesscard.entities.Contact;
import com.algofusion.businesscard.requests.ContactRequest;
import com.algofusion.businesscard.responses.ContactResponse;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    @Mapping(source = "businessCard.id", target = "businessCardId")
    ContactResponse toContactResponse(Contact contact);

    @Mapping(target = "businessCard", ignore = true)
    Contact toContact(ContactRequest contactRequest);
}
