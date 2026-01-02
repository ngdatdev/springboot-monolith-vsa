package com.vsa.ecommerce.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Security utilities for accessing current authenticated user.
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the current authenticated user's email.
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getEmail);
    }

    /**
     * Get the current authenticated user's ID.
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getId);
    }

    /**
     * Get the current UserPrincipal.
     */
    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }

        return Optional.empty();
    }

    /**
     * Check if a user is authenticated.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Check if current user has a specific role.
     */
    public static boolean hasRole(String role) {
        return getCurrentUserPrincipal()
                .map(user -> user.getRoleNames().contains(role))
                .orElse(false);
    }

}
