package com.vsa.ecommerce.config.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cloudinary configuration properties.
 * Maps to cloudinary.* in application.yml
 */
@Component
@ConfigurationProperties(prefix = "cloudinary")
@Getter
@Setter
public class CloudinaryProperties {

    /**
     * Cloudinary cloud name.
     */
    private String cloudName;

    /**
     * Cloudinary API key.
     */
    private String apiKey;

    /**
     * Cloudinary API secret.
     */
    private String apiSecret;

    /**
     * Default folder for uploads.
     */
    private String defaultFolder = "vsa-ecommerce";
}
