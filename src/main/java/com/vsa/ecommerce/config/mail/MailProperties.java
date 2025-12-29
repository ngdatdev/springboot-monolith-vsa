package com.vsa.ecommerce.config.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol = "smtp";
    private String defaultEncoding = "UTF-8";
    private String from;
    private Map<String, Object> properties = new HashMap<>();

    public String getFromAddress() {
        return from != null ? from : username;
    }
}
