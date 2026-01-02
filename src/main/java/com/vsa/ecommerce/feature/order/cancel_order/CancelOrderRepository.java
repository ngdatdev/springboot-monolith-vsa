package com.vsa.ecommerce.feature.order.cancel_order;

import com.vsa.ecommerce.domain.entity.Inventory;
import com.vsa.ecommerce.domain.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureCancelOrderRepository")
public class CancelOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Order> findOrderById(Long id) {
        return Optional.ofNullable(entityManager.find(Order.class, id));
    }

    public Optional<Inventory> findInventoryByProductId(Long productId) {
        try {
            Inventory inventory = entityManager.createQuery(
                    "SELECT i FROM Inventory i WHERE i.productId = :productId", Inventory.class)
                    .setParameter("productId", productId)
                    .getSingleResult();
            return Optional.of(inventory);
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }

    public void saveInventory(Inventory inventory) {
        if (inventory.getId() == null) {
            entityManager.persist(inventory);
        } else {
            entityManager.merge(inventory);
        }
    }

    public void saveOrder(Order order) {
        if (order.getId() == null) {
            entityManager.persist(order);
        } else {
            entityManager.merge(order);
        }
    }
}
