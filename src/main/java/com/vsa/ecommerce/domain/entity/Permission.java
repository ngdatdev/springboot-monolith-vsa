package com.vsa.ecommerce.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Permission entity for fine-grained access control.
 * 
 * Permission Format: {resource}:{action}
 * 
 * Examples:
 * - order:read
 * - order:write
 * - order:delete
 * - product:read
 * - product:write
 * - user:admin
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Permission name in format: resource:action
     * e.g., "order:read", "product:write"
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Resource type (e.g., "order", "product", "user")
     */
    @Column(nullable = false, length = 50)
    private String resource;

    /**
     * Action type (e.g., "read", "write", "delete", "admin")
     */
    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 255)
    private String description;

    /**
     * Many-to-many relationship with Role.
     * A permission can be assigned to multiple roles.
     */
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Permission(String name, String resource, String action, String description) {
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Build permission name from resource and action.
     */
    public static String buildPermissionName(String resource, String action) {
        return resource + ":" + action;
    }
}
