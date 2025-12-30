package com.vsa.ecommerce.feature.auth.change_password;

import com.vsa.ecommerce.common.repository.BaseRepository;
import com.vsa.ecommerce.domain.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangePasswordRepository extends BaseRepository<User, Long> {
}
