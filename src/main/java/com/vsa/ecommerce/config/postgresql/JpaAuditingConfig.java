package com.vsa.ecommerce.config.postgresql;

import com.vsa.ecommerce.common.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA Configuration for the PostgreSQL database.
 * Enables JPA Auditing to automatically populate created_by and updated_by
 * fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Provides the current auditor (user) for JPA Auditing.
     * Uses the current authenticated user's email from the Security Context.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Optional<String> email = SecurityUtils.getCurrentUserEmail();
            return Optional.of(email.orElse("SYSTEM"));
        };
    }
}
