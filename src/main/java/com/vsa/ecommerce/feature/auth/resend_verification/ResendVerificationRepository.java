package com.vsa.ecommerce.feature.auth.resend_verification;

import com.vsa.ecommerce.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureResendVerificationRepository")
public class ResendVerificationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.of(entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }
}
