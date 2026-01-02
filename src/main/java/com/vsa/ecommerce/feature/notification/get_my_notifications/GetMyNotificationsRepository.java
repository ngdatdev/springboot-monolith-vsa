package com.vsa.ecommerce.feature.notification.get_my_notifications;

import com.vsa.ecommerce.domain.entity.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("FeatureGetMyNotificationsRepository")
public class GetMyNotificationsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Notification> findAllByUserId(Long userId, int page, int size) {
        return entityManager
                .createQuery("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("userId", userId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countByUserId(Long userId) {
        return entityManager.createQuery("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
