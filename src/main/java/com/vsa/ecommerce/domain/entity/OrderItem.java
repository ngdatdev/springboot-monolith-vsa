package com.vsa.ecommerce.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Represents a line item within an order.
 * Demonstrates the pattern of snapshotting data (price/name) vs referencing data (Product).
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Hard reference to Product.
     * This is convenient in a ecommerce but painful when Product moves to a Catalog Service.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Snapshot of the product name at the time of purchase.
     * Used because product names might change later.
     */
    @Column(nullable = false)
    private String productNameSnapshot;

    /**
     * Snapshot of the price per unit at the time of purchase.
     */
    @Column(nullable = false)
    private BigDecimal pricePerUnitSnapshot;

    @Column(nullable = false)
    private Integer quantity;

    public OrderItem() {}
}
