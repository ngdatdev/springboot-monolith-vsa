package com.vsa.ecommerce.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private boolean ssl;
    private Duration timeout;
}