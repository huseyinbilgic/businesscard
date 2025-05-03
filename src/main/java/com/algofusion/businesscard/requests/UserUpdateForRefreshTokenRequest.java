package com.algofusion.businesscard.requests;

import java.time.Instant;

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
public class UserUpdateForRefreshTokenRequest {
    private String refreshToken;
    private Instant refreshTokenExpiresAt;
}
