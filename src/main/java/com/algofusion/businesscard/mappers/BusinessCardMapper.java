package com.algofusion.businesscard.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.algofusion.businesscard.entities.BusinessCard;
import com.algofusion.businesscard.requests.BusinessCardRequest;
import com.algofusion.businesscard.responses.BusinessCardResponse;

@Mapper(componentModel = "spring", uses = { ContactMapper.class })
public interface BusinessCardMapper {
    @Mapping(source = "user.id", target = "userId")
    BusinessCardResponse toBusinessCardResponse(BusinessCard businessCard);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bcCode", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "businessCardPrivacies", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "contactsRequests", target = "contacts")
    BusinessCard toBusinessCard(BusinessCardRequest businessCardRequest);
}
