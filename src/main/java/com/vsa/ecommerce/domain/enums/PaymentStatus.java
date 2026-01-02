package com.vsa.ecommerce.domain.enums;

public enum PaymentStatus {
    PENDING, // Payment initiated
    PROCESSING, // Gateway processing
    AUTHORIZED, // Funds authorized (not captured yet)
    CAPTURED, // Funds captured successfully
    FAILED, // Payment failed
    CANCELLED, // Payment cancelled
    REFUND_PENDING, // Refund initiated
    REFUNDED, // Fully refunded
    PARTIALLY_REFUNDED // Partial refund
}
