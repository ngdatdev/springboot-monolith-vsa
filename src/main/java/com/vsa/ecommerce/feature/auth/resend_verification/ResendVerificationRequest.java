package com.vsa.ecommerce.feature.auth.resend_verification;

import com.vsa.ecommerce.common.abstraction.Request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationRequest implements Request {
    @NotBlank
    @Email
    private String email;
}
