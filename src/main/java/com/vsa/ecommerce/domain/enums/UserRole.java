package com.vsa.ecommerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Standard User Roles in the system.
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    SUPER_ADMIN("Full system access"),
    ADMIN("Administrative access"),
    MANAGER("Management access"),
    USER("Standard user access"),
    GUEST("Read-only access");

    private final String description;
}
