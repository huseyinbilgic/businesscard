package com.algofusion.businesscard.requests;

import com.algofusion.businesscard.enums.ContactType;

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
public class ContactRequest {
    private Long id;

    @NotNull(message = "ContactType cannot be null.")
    private ContactType contactType;

    @NotNull(message = "Label cannot be null.")
    @NotBlank(message = "Label cannot be blank")
    @Size(max = 255)
    private String label;

    @NotNull(message = "Value cannot be null.")
    @NotBlank(message = "Value cannot be blank")
    @Size(max = 255)
    private String contactValue;
}
