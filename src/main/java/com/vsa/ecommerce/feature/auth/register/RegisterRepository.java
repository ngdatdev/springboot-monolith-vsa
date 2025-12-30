package com.vsa.ecommerce.feature.auth.register;

import com.vsa.ecommerce.common.repository.BaseRepository;
import com.vsa.ecommerce.domain.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisterRepository extends BaseRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
