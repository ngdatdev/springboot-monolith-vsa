package com.vsa.ecommerce.common.security.repository;

import com.vsa.ecommerce.common.repository.BaseRepository;
import com.vsa.ecommerce.domain.entity.Role;
import com.vsa.ecommerce.domain.enums.UserRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityRoleRepository extends BaseRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
}
