package com.vsa.ecommerce.feature.auth.forgot_password;

import com.vsa.ecommerce.common.abstraction.Request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest implements Request {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
