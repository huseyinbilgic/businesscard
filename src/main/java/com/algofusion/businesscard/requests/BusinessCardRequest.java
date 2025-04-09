package com.algofusion.businesscard.requests;

import java.util.List;

import com.algofusion.businesscard.enums.PrivacyStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class BusinessCardRequest {
    @NotNull(message = "Fullname cannot be null.")
    @NotBlank(message = "Fullname cannot be blank")
    @Size(max = 255)
    private String fullName;

    @Size(max = 255)
    private String company;

    @Size(max = 255)
    private String jobTitle;

    private String aboutIt;

    @NotNull(message = "Privacy cannot be null.")
    private PrivacyStatus privacy;

    @NotNull(message = "Contacts cannot be null.")
    private List<ContactRequest> contactsRequests;
}
