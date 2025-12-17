package com.vsa.monolith.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks stock for a specific product.
 * Tightly coupled to Product via a One-to-One or Many-to-One relationship.
 * Optimistic locking is CRITICAL here to prevent overselling.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Direct hard reference to the Product entity.
     * Splitting this later into an Inventory Service will require breaking this FK.
     */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    /**
     * Quantity physically available in the warehouse.
     */
    @Column(nullable = false)
    private Integer availableQuantity;

    /**
     * Quantity reserved for orders that are placed but not yet shipped/completed.
     */
    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    /**
     * Version field for optimistic locking.
     * Prevents race conditions when two orders try to reserve stock simultaneously.
     */
    @Version
    private Long version;

    public Inventory() {}
}
