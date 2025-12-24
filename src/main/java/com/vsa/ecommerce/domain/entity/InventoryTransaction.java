package com.vsa.ecommerce.domain.entity;

import com.vsa.ecommerce.domain.enums.InventoryTransactionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable audit log of all inventory changes.
 * Cross-domain reference example: It links to both Inventory (Core) and Order (Sales).
 */
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    /**
     * Determines why the stock changed.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryTransactionType type;

    @Column(nullable = false)
    private Integer quantityChange;

    @Column(nullable = false)
    private Integer quantityBefore;

    @Column(nullable = false)
    private Integer quantityAfter;

    /**
     * Optional link to an Order if this change was triggered by a sale/refund.
     * This creates a cross-domain dependency that is hard to untangle.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order relatedOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public InventoryTransaction() {}

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
