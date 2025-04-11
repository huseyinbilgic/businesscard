package com.algofusion.businesscard.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class RegisterUserRequest {

    @NotNull(message = "Email cannot be null.")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255)
    @Pattern(regexp = "\\S+", message = "Email cannot contain spaces!")
    private String email;

    @NotNull(message = "Username cannot be null.")
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 255)
    @Pattern(regexp = "\\S+", message = "Username cannot contain spaces!")
    private String username;

    @NotNull(message = "Password cannot be null.")
    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "\\S+", message = "Password cannot contain spaces!")
    private String password;
}
