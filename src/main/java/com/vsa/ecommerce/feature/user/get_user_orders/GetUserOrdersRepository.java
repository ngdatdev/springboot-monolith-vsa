package com.vsa.ecommerce.feature.user.get_user_orders;

import com.vsa.ecommerce.domain.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("GetUserOrdersRepository")
public class GetUserOrdersRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Order> findOrdersByUserId(Long userId) {
        return entityManager
                .createQuery("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC", Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
