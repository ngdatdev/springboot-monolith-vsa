package com.vsa.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vsa.ecommerce.common.domain.BaseEntity;
import com.vsa.ecommerce.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a customer in the system.
 * Users are the root of most business operations.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String phoneNumber;

    /**
     * BCrypt encoded password for authentication.
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /**
     * Many-to-many relationship with Role.
     * A user can have multiple roles.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Spring Security UserDetails fields for account status.
     */
    @Column(name = "accountnonexpired", nullable = false)
    private Boolean accountNonExpired = true;

    @Column(name = "accountnonlocked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "credentialsnonexpired", nullable = false)
    private Boolean credentialsNonExpired = true;

    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * One user can place multiple orders.
     * This field creates a bi-directional navigation which can be expensive if
     * fetched eagerly.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Order> orders = new ArrayList<>();

    // Use optimistic locking even for Users if profile updates are frequent
    @Version
    private Long version;
}
