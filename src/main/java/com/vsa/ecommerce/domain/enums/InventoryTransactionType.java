package com.vsa.ecommerce.domain.enums;

public enum InventoryTransactionType {
    // Inbound
    STOCK_IN, // New stock received
    STOCK_ADJUSTMENT, // Manual adjustment by admin
    RETURN_RECEIVED, // Customer return

    // Outbound
    RESERVE, // Order created (reserve stock)
    RELEASE, // Order cancelled (release reservation)
    FULFILL, // Order shipped (deduct from available)

    // Loss
    DAMAGE, // Damaged goods
    LOST, // Lost inventory
    EXPIRED // Expired products
}
