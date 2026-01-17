package com.vsa.ecommerce.domain.entity;

import com.vsa.ecommerce.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Generic entity for background tasks and system events.
 * Uses JSON-like columns to store flexible data.
 */
@Entity
@Table(name = "persistent_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersistentTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // e.g., "EMAIL", "NOTIFICATION", "SYSTEM_LOG"

    @Column(nullable = false)
    private String taskType; // Specific subtype

    @Column(nullable = false)
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED

    /**
     * The main data for the task in JSON format.
     */
    @Column(columnDefinition = "TEXT")
    private String payload;

    /**
     * Execution metadata in JSON format (retry count, errors, etc.)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Builder.Default
    @Column(nullable = false)
    private Integer priority = 0;

    @Column
    private LocalDateTime scheduledAt;

    @Column
    private LocalDateTime processedAt;

    @Builder.Default
    @Column
    private Integer retryCount = 0;

    @Column(length = 2000)
    private String lastError;

    @Version
    private Long version;
}
