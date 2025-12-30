package com.vsa.ecommerce.feature.auth.me;

import com.vsa.ecommerce.common.repository.BaseRepository;
import com.vsa.ecommerce.domain.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface MeRepository extends BaseRepository<User, Long> {
}
