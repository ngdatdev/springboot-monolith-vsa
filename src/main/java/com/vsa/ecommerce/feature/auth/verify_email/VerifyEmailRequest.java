package com.vsa.ecommerce.feature.auth.verify_email;

import com.vsa.ecommerce.common.abstraction.Request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for email verification.
 */
@Getter
@Setter
public class VerifyEmailRequest implements Request {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "OTP code is required")
    private String code;
}
