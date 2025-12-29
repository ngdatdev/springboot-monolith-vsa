package com.vsa.ecommerce.config.redis;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.username}")
    private String username;
    @Value("${redis.password}")
    private String password;
    @Value("${redis.ssl.enabled}")
    private boolean ssl;
    @Value("${redis.database:0}")
    private int database;
    @Value("${redis.timeout}")
    private Duration timeout;
    @Value("${redis.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${redis.lettuce.pool.max-wait}")
    private Duration maxWait;
}