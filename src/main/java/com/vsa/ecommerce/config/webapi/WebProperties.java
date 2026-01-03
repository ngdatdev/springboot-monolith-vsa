package com.vsa.ecommerce.config.webapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Common Auth properties, such as frontend URLs.
 */
@Component
@ConfigurationProperties(prefix = "app.auth")
@Getter
@Setter
public class WebProperties {

    /**
     * Frontend base URL for links (e.g., http://localhost:3000).
     */
    private String frontendUrl = "http://localhost:3000";

    /**
     * Path for password reset (e.g., /reset-password).
     */
    private String passwordResetPath = "/reset-password";

    /**
     * Path for email verification (e.g., /verify-email).
     */
    private String emailVerificationPath = "/verify-email";

    public String getPasswordResetUrl() {
        return frontendUrl + passwordResetPath;
    }

    public String getEmailVerificationUrl() {
        return frontendUrl + emailVerificationPath;
    }
}
