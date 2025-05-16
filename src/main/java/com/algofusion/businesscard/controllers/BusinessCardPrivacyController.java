package com.algofusion.businesscard.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.dto.PrivacyUser;
import com.algofusion.businesscard.services.BusinessCardPrivacyService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/business-cards-privacy/")
@RequiredArgsConstructor
public class BusinessCardPrivacyController {
    private final BusinessCardPrivacyService businessCardPrivacyService;

    @GetMapping("business-card/{id}")
    public ResponseEntity<List<PrivacyUser>> fetchPrivacyUsersByBusinessCardId(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(businessCardPrivacyService.fetchPrivacyUsersByBusinessCardId(username, id));
    }

    @PostMapping("business-card/{id}")
    public ResponseEntity<String> updateBusinessCardPrivacy(@PathVariable Long id,
            @RequestBody @NotNull(message = "Used Ids can not be null") Set<Long> userIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(businessCardPrivacyService.updateBusinessCardPrivacy(username, id, userIds));
    }
}
