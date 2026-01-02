package com.vsa.ecommerce.domain.entity;

import com.vsa.ecommerce.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tracks stock for a specific product.
 * Tightly coupled to Product via a One-to-One or Many-to-One relationship.
 * Optimistic locking is CRITICAL here to prevent overselling.
 */
@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory extends BaseEntity {

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

    /**
     * Version field for optimistic locking.
     * Prevents race conditions when two orders try to reserve stock simultaneously.
     */
    @Version
    private Long version;

    // --- Business Logic ---

    public void reserve(int quantity) {
        if (availableQuantity < quantity) {
            throw new com.vsa.ecommerce.common.exception.BusinessException(
                    com.vsa.ecommerce.common.exception.BusinessStatus.INSUFFICIENT_STOCK);
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    public void release(int quantity) {
        this.reservedQuantity -= quantity;
        this.availableQuantity += quantity;
        // Logic to bound at 0 or handle logic errors could be added, but assuming valid
        // calls from trusted services.
        if (this.reservedQuantity < 0) {
            this.reservedQuantity = 0; // Self-correction or throw error
        }
    }
}
