package com.vsa.ecommerce.feature.auth.forgot_password;

import com.vsa.ecommerce.common.repository.BaseRepository;
import com.vsa.ecommerce.domain.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends BaseRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
