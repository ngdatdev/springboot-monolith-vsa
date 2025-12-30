package com.vsa.ecommerce.config;

import com.vsa.ecommerce.domain.entity.Permission;
import com.vsa.ecommerce.domain.entity.Role;
import com.vsa.ecommerce.domain.enums.AppPermission;
import com.vsa.ecommerce.domain.enums.UserRole;
import com.vsa.ecommerce.domain.repository.PermissionRepository;
import com.vsa.ecommerce.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Seeds the database with initial Roles and Permissions on startup.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data initialization...");

        // 1. Initialize Permissions
        initializePermissions();

        // 2. Initialize Roles and link to permissions
        initializeRoles();

        log.info("Data initialization completed.");
    }

    private void initializePermissions() {
        for (AppPermission appPerm : AppPermission.values()) {
            if (permissionRepository.findByName(appPerm.getName()).isEmpty()) {
                log.info("Creating permission: {}", appPerm.getName());
                permissionRepository.save(Permission.fromAppPermission(appPerm));
            }
        }
    }

    private void initializeRoles() {
        // Get all created permissions
        Set<Permission> allPermissions = permissionRepository.findAll().stream().collect(Collectors.toSet());

        for (UserRole roleEnum : UserRole.values()) {
            roleRepository.findByName(roleEnum).ifPresentOrElse(
                    role -> log.debug("Role {} already exists", roleEnum),
                    () -> {
                        log.info("Creating role: {}", roleEnum);
                        Role role = new Role(roleEnum, roleEnum.getDescription());

                        // Logic for default permission assignment
                        assignPermissionsToRole(role, roleEnum, allPermissions);

                        roleRepository.save(role);
                    });
        }
    }

    private void assignPermissionsToRole(Role role, UserRole roleEnum, Set<Permission> allPermissions) {
        switch (roleEnum) {
            case SUPER_ADMIN:
                role.setPermissions(allPermissions);
                break;
            case ADMIN:
                // Admins get everything except maybe sensitive user management or system logs
                role.setPermissions(allPermissions);
                break;
            case MANAGER:
                // Managers handle orders and inventory
                role.setPermissions(allPermissions.stream()
                        .filter(p -> p.getResource().equals("order") || p.getResource().equals("inventory"))
                        .collect(Collectors.toSet()));
                break;
            case USER:
                // Users can only read products and manage their own orders
                role.setPermissions(allPermissions.stream()
                        .filter(p -> p.getName().equals("product:read") || p.getName().equals("order:read")
                                || p.getName().equals("order:write"))
                        .collect(Collectors.toSet()));
                break;
            default:
                // Guests or others
                role.setPermissions(allPermissions.stream()
                        .filter(p -> p.getName().endsWith(":read"))
                        .collect(Collectors.toSet()));
                break;
        }
    }
}
