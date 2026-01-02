package com.vsa.ecommerce.feature.notification.send_notification;

import com.vsa.ecommerce.domain.entity.Notification;
import com.vsa.ecommerce.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureSendNotificationRepository")
public class SendNotificationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public void save(Notification notification) {
        entityManager.persist(notification);
    }
}
