package com.vsa.ecommerce.domain.entity;

import com.vsa.ecommerce.common.domain.BaseEntity;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The central entity of the system.
 * It has tendrils into User, Payments, and OrderItems.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Version
    private Long version;

    private String trackingNumber;
    private String carrier;
    private String cancelReason;

    // --- Business Logic / State Machine ---

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(item -> item.getPricePerUnitSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markPaid() {
        validateTransition(OrderStatus.CONFIRMED);
        this.status = OrderStatus.CONFIRMED;
    }

    public void markProcessing() {
        validateTransition(OrderStatus.PROCESSING);
        this.status = OrderStatus.PROCESSING;
    }

    public void markReadyToShip() {
        validateTransition(OrderStatus.READY_TO_SHIP);
        this.status = OrderStatus.READY_TO_SHIP;
    }

    public void markShipped(String trackingNumber, String carrier) {
        validateTransition(OrderStatus.SHIPPED);
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
    }

    public void markDelivered() {
        validateTransition(OrderStatus.DELIVERED);
        this.status = OrderStatus.DELIVERED;
    }

    public void complete() {
        validateTransition(OrderStatus.COMPLETED);
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel(String reason) {
        // Validation: Cannot cancel if already shipped (unless returned)
        if (isAfterShipping()) {
            throw new IllegalStateException("Order cannot be cancelled after shipping. Request return instead.");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelReason = reason;
    }

    private boolean isAfterShipping() {
        return status == OrderStatus.SHIPPED || status == OrderStatus.OUT_FOR_DELIVERY
                || status == OrderStatus.DELIVERED || status == OrderStatus.COMPLETED;
    }

    public void requestReturn(String reason) {
        if (status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Can only request return for delivered orders.");
        }
        this.status = OrderStatus.RETURN_REQUESTED;
        this.cancelReason = reason; // Reuse cancelReason for return reason
    }

    public void approveReturn() {
        validateTransition(OrderStatus.RETURNED);
        this.status = OrderStatus.RETURNED;
    }

    public void rejectReturn() {
        // Revert to DELIVERED if rejected
        if (status != OrderStatus.RETURN_REQUESTED) {
            throw new IllegalStateException("Can only reject return if return was requested.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    private void validateTransition(OrderStatus newStatus) {
        // Simplified validation logic - expand based on full state machine chart
        // For now, allow flow: PENDING -> PAID -> PROCESSING -> READY -> SHIPPED ->
        // DELIVERED -> COMPLETED
    }
}
