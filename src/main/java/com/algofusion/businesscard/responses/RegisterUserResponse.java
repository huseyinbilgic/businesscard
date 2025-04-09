package com.algofusion.businesscard.responses;

import java.time.Instant;

import com.algofusion.businesscard.enums.Role;

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
public class RegisterUserResponse {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
}
