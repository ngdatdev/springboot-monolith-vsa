package com.vsa.ecommerce.common.id;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Snowflake ID Generator configuration.
 */
@Configuration
@RequiredArgsConstructor
public class SnowflakeConfig {

    private final com.vsa.ecommerce.common.id.SnowflakeProperties snowflakeProperties;

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator(
                snowflakeProperties.getDatacenterId(),
                snowflakeProperties.getWorkerId());
    }
}
