package com.vsa.ecommerce.feature.auth.login;

import com.vsa.ecommerce.common.abstraction.IRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Login request DTO.
 */
@Getter
@Setter
public class LoginRequest implements IRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
