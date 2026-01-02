package com.vsa.ecommerce.feature.order.list_orders;

import com.vsa.ecommerce.domain.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("FeatureListOrdersRepository")
public class ListOrdersRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Order> findByUserId(Long userId, int page, int size) {
        return entityManager
                .createQuery("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC", Order.class)
                .setParameter("userId", userId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
