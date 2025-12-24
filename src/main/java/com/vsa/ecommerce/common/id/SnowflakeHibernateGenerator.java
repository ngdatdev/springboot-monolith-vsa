package com.vsa.ecommerce.common.id;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Hibernate custom ID generator using Snowflake algorithm.
 * 
 * Usage in entity:
 * 
 * <pre>
 * &#64;Id
 * &#64;GeneratedValue(generator = "snowflake")
 * @GenericGenerator(name = "snowflake", strategy = "com.vsa.ecommerce.common.id.SnowflakeHibernateGenerator")
 * private Long id;
 * </pre>
 */
@Component
public class SnowflakeHibernateGenerator implements IdentifierGenerator {

    private static SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * Set the Snowflake ID generator.
     * Called by Spring after bean initialization.
     */
    public static void setSnowflakeIdGenerator(SnowflakeIdGenerator generator) {
        snowflakeIdGenerator = generator;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        if (snowflakeIdGenerator == null) {
            throw new IllegalStateException(
                    "SnowflakeIdGenerator not initialized. Make sure SnowflakeConfig is loaded.");
        }
        return snowflakeIdGenerator.nextId();
    }
}
