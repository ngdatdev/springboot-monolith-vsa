package com.vsa.ecommerce.feature.auth.refresh_token;

import com.vsa.ecommerce.common.abstraction.Request;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for refreshing access token.
 */
@Getter
@Setter
public class RefreshTokenRequest implements Request {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    private Long userId; // Optional or extracted from token
}
