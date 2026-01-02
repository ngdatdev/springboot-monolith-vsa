package com.vsa.ecommerce.feature.notification.mark_read;

import com.vsa.ecommerce.domain.entity.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureMarkReadRepository")
public class MarkReadRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Notification.class, id));
    }

    public void save(Notification notification) {
        entityManager.merge(notification);
    }
}
