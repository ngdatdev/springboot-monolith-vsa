package com.vsa.ecommerce.config;

import com.vsa.ecommerce.domain.entity.Role;
import com.vsa.ecommerce.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the database with initial Roles on startup.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final com.vsa.ecommerce.common.security.repository.SecurityRoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data initialization...");

        // Initialize Roles
        initializeRoles();

        log.info("Data initialization completed.");
    }

    private void initializeRoles() {
        for (UserRole roleEnum : UserRole.values()) {
            roleRepository.findByName(roleEnum).ifPresentOrElse(
                    role -> log.debug("Role {} already exists", roleEnum),
                    () -> {
                        log.info("Creating role: {}", roleEnum);
                        Role role = new Role(roleEnum, roleEnum.getDescription());
                        roleRepository.save(role);
                    });
        }
    }
}
