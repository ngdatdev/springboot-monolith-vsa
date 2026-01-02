package com.vsa.ecommerce.feature.auth.logout;

import com.vsa.ecommerce.domain.entity.TokenBlacklist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository("FeatureLogoutRepository")
public class LogoutRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(TokenBlacklist blacklist) {
        entityManager.persist(blacklist);
    }
}
