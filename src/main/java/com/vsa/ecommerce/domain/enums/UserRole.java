package com.vsa.ecommerce.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Standard User Roles in the system.
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("Administrative access"),
    USER("Standard user access"),
    GUEST("Read-only access");

    private final String description;
}
