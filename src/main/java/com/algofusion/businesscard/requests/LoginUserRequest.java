package com.algofusion.businesscard.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class LoginUserRequest {

    @NotNull(message = "Username cannot be null.")
    @NotBlank(message = "Username cannot be blank")
    private String usernameOrEmail;

    @NotNull(message = "Password cannot be null.")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
