package com.vsa.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsa.ecommerce.common.domain.BaseEntity;
import com.vsa.ecommerce.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Role entity for role-based access control (RBAC).
 * 
 * Role Hierarchy:
 * - SUPER_ADMIN: Full system access
 * - ADMIN: Administrative access
 * - MANAGER: Management access
 * - USER: Standard user access
 * - GUEST: Read-only access
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private UserRole name;

    @Column(length = 255)
    private String description;

    /**
     * Many-to-many relationship with User.
     * A role can be assigned to multiple users.
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Version
    private Long version;

    public Role(UserRole name, String description) {
        this.name = name;
        this.description = description;
    }
}
