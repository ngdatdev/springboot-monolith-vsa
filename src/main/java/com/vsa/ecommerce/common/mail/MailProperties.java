package com.vsa.ecommerce.common.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mail configuration properties.
 * Maps to spring.mail.* in application.yml
 */
@Component
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class MailProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String from;

    /**
     * Email address that appears in "From" field.
     * If not set, uses username.
     */
    public String getFromAddress() {
        return from != null ? from : username;
    }
}
