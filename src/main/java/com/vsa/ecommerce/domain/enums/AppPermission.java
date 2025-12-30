package com.vsa.ecommerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * System-wide Permission Definitions.
 * Format: {resource}:{action}
 */
@Getter
@RequiredArgsConstructor
public enum AppPermission {
    // Order Permissions
    ORDER_READ("order", "read", "View order details"),
    ORDER_WRITE("order", "write", "Create or edit orders"),
    ORDER_DELETE("order", "delete", "Remove orders"),

    // Product Permissions
    PRODUCT_READ("product", "read", "View product catalog"),
    PRODUCT_WRITE("product", "write", "Manage products"),

    // Inventory Permissions
    INVENTORY_READ("inventory", "read", "View stock levels"),
    INVENTORY_WRITE("inventory", "write", "Adjust stock levels"),

    // User Management
    USER_ADMIN("user", "admin", "Full user management");

    private final String resource;
    private final String action;
    private final String description;

    public String getName() {
        return resource + ":" + action;
    }
}
