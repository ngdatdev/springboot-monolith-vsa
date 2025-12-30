package com.vsa.ecommerce.feature.auth.refresh_token;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for refresh token request.
 */
@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenResponse implements Response {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Long refreshExpiresIn;

    public RefreshTokenResponse(String accessToken, String refreshToken, Long expiresIn, Long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
    }
}
