package com.vsa.ecommerce.config.id;

import com.vsa.ecommerce.common.id.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Snowflake ID Generator configuration.
 */
@Configuration
@RequiredArgsConstructor
public class SnowflakeConfig {

    private final SnowflakeProperties snowflakeProperties;

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator(
                snowflakeProperties.getDatacenterId(),
                snowflakeProperties.getWorkerId());
    }
}
