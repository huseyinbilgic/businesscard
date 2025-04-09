package com.algofusion.businesscard.responses;

import java.time.Instant;
import java.util.List;

import com.algofusion.businesscard.enums.PrivacyStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessCardResponse {
    private Long id;
    private Long userId;
    private String bcCode;
    private String fullName;
    private String company;
    private String jobTitle;
    private String aboutIt;
    private PrivacyStatus privacy;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ContactResponse> contacts;
}
