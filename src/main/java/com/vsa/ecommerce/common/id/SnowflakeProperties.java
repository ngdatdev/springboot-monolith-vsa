package com.vsa.ecommerce.common.id;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Snowflake ID configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "snowflake")
@Getter
@Setter
public class SnowflakeProperties {

    /**
     * Datacenter ID (0-31).
     * Should be unique per datacenter.
     */
    private Long datacenterId = 0L;

    /**
     * Worker/Machine ID (0-31).
     * Should be unique per machine within a datacenter.
     */
    private Long workerId = 0L;
}
