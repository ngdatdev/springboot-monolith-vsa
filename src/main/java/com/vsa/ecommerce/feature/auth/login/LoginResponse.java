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
@AllArgsConstructor
public class LoginResponse implements Response {

    /**
     * JWT access token.
     */
    private String accessToken;

    /**
     * Token type (always "Bearer").
     */
    private String tokenType = "Bearer";

    /**
     * Token expiration time in seconds.
     */
    private Long expiresIn;

    /**
     * User information.
     */
    private UserInfo user;

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
        private Set<String> permissions;
    }

    public LoginResponse(String accessToken, Long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
