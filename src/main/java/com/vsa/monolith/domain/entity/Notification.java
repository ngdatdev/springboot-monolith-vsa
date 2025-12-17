package com.vsa.monolith.domain.entity;

import com.vsa.monolith.domain.enums.NotificationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores notifications to be sent or already sent.
 * Another example of cross-domain coupling (User + Order).
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    /**
     * Ideally, this should just be an "orderId" string or long to decouple,
     * but in a monolith, we often just directly reference the entity for convenience.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order relatedOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    /**
     * Weakly typed payload (JSON) for flexibility.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payloadJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;

    public Notification() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
