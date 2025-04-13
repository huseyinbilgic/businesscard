package com.algofusion.businesscard.responses;

import java.io.Serializable;

import com.algofusion.businesscard.enums.ContactType;

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
public class ContactResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long businessCardId;
    private ContactType contactType;
    private String label;
    private String contactValue;
}
