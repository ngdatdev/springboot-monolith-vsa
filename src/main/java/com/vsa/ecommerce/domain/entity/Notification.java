package com.vsa.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsa.ecommerce.common.domain.BaseEntity;
import com.vsa.ecommerce.domain.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stores notifications to be sent or already sent.
 * Another example of cross-domain coupling (User + Order).
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    /**
     * Ideally, this should just be an "orderId" string or long to decouple,
     * but in a ecommerce, we often just directly reference the entity for
     * convenience.
     */
    @JsonIgnore
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

    private LocalDateTime sentAt;
}
