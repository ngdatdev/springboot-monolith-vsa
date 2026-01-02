package com.vsa.ecommerce.feature.order.admin_update_status;

import com.vsa.ecommerce.domain.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureUpdateOrderStatusRepository")
public class UpdateOrderStatusRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Order.class, id));
    }

    public void save(Order order) {
        entityManager.merge(order);
    }
}
