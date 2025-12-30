package com.vsa.ecommerce.feature.auth.verify_email;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for email verification.
 */
@Getter
@Setter
@AllArgsConstructor
public class VerifyEmailResponse implements Response {
    private String message;
}
