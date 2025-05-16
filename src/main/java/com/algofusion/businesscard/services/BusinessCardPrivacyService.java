package com.algofusion.businesscard.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.dto.PrivacyUser;
import com.algofusion.businesscard.entities.BusinessCard;
import com.algofusion.businesscard.entities.BusinessCardPrivacy;
import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.errors.CustomException;
import com.algofusion.businesscard.helper.PermissionHelper;
import com.algofusion.businesscard.repositories.BusinessCardPrivacyRepository;
import com.algofusion.businesscard.repositories.BusinessCardRepository;
import com.algofusion.businesscard.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCardPrivacyService {
    private final BusinessCardPrivacyRepository businessCardPrivacyRepository;
    private final BusinessCardRepository businessCardRepository;
    private final UserRepository userRepository;

    public List<PrivacyUser> fetchPrivacyUsersByBusinessCardId(String username, Long id) {
        BusinessCard byId = businessCardRepository.findById(id)
                .orElseThrow(() -> new CustomException("Business Card not found" + id));

        PermissionHelper.checkUsername(byId.getUser().getUsername(), username);
        return businessCardPrivacyRepository
                .findPrivacyUsersByBusinessCardId(id);
    }

    public String updateBusinessCardPrivacy(String username, Long id, Set<Long> userIds) {
        BusinessCard businessCard = businessCardRepository.findById(id)
                .orElseThrow(() -> new CustomException("Business Card not found" + id));

        PermissionHelper.checkUsername(businessCard.getUser().getUsername(), username);

        List<BusinessCardPrivacy> toBeRemoved = businessCard.getBusinessCardPrivacies().stream()
                .filter(p -> !userIds.contains(p.getUser().getId()))
                .collect(Collectors.toList());
        businessCard.getBusinessCardPrivacies().removeAll(toBeRemoved);

        Set<Long> existingIds = businessCard.getBusinessCardPrivacies().stream()
                .map(p -> p.getUser().getId())
                .collect(Collectors.toSet());

        List<BusinessCardPrivacy> newPrivacies = userIds.stream()
                .filter(i -> !existingIds.contains(i))
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException("User not found" + userId));
                    return BusinessCardPrivacy.builder()
                            .businessCard(businessCard)
                            .user(user)
                            .build();
                })
                .collect(Collectors.toList());

        businessCardPrivacyRepository.saveAll(newPrivacies);

        return "Added users to Business Card";
    }

}
