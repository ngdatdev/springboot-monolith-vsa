package com.vsa.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsa.ecommerce.common.domain.BaseEntity;
import com.vsa.ecommerce.domain.enums.AppPermission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission entity for fine-grained access control.
 * 
 * Permission Format: {resource}:{action}
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

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

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    @Version
    private Long version;

    public Permission(String name, String resource, String action, String description) {
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    /**
     * Create Permission from AppPermission enum.
     */
    public static Permission fromAppPermission(AppPermission appPermission) {
        return new Permission(
                appPermission.getName(),
                appPermission.getResource(),
                appPermission.getAction(),
                appPermission.getDescription());
    }

    /**
     * Build permission name from resource and action.
     */
    public static String buildPermissionName(String resource, String action) {
        return resource + ":" + action;
    }
}
