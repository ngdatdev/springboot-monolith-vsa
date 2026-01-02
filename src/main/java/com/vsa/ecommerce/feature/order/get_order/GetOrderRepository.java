package com.vsa.ecommerce.feature.order.get_order;

import com.vsa.ecommerce.domain.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureGetOrderRepository")
public class GetOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Order> findById(Long id) {
        // Fetch graph hint could be optimized here to eager load items
        // but for now relying on default lazy loading + transaction
        return Optional.ofNullable(entityManager.find(Order.class, id));
    }
}
