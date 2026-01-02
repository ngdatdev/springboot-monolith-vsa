package com.vsa.ecommerce.domain.enums;

public enum OrderStatus {
    // Customer actions
    PENDING_PAYMENT, // Order created, awaiting payment

    // Payment processing
    PAYMENT_PROCESSING, // Payment gateway processing
    PAYMENT_FAILED, // Payment failed, can retry

    // Fulfillment stages
    CONFIRMED, // Payment successful, order confirmed
    PROCESSING, // Warehouse preparing order
    READY_TO_SHIP, // Order packed, ready for pickup
    SHIPPED, // Order handed to carrier
    OUT_FOR_DELIVERY, // Last mile delivery
    DELIVERED, // Customer received

    // Terminal states
    COMPLETED, // Order finalized (after return window)
    CANCELLED, // Cancelled by user/admin
    REFUNDED, // Payment refunded
    RETURNED, // Product returned
    RETURN_REQUESTED // Return requested by user
}
