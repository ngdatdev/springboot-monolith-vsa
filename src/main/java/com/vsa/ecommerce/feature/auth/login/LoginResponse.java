package com.vsa.ecommerce.feature.auth.login;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Login response containing JWT token and user information.
 */
@Getter
@Setter
public class LoginResponse implements Response {

    /**
     * JWT access token.
     */
    private String accessToken;

    /**
     * JWT refresh token.
     */
    private String refreshToken;

    /**
     * Token type (always "Bearer").
     */
    private String tokenType = "Bearer";

    /**
     * Token expiration time in seconds.
     */
    private Long expiresIn;

    /**
     * Refresh token expiration time in seconds.
     */
    private Long refreshExpiresIn;

    /**
     * User information.
     */
    private UserInfo user;

    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, Long refreshExpiresIn,
            UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.user = user;
    }

    /**
     * Nested UserInfo class.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private Set<String> roles;
    }
}
