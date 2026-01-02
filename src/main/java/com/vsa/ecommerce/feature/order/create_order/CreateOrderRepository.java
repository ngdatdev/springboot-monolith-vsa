package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.domain.entity.Inventory;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.domain.entity.Product;
import com.vsa.ecommerce.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Single Repository for Create Order Feature using EntityManager directly.
 * 
 * Replaces multiple JPA repositories.
 */
@Repository("FeatureCreateOrderRepository")
public class CreateOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public Optional<Product> findProductById(Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
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

    public Order saveOrder(Order order) {
        if (order.getId() == null) {
            entityManager.persist(order);
            return order;
        } else {
            return entityManager.merge(order);
        }
    }
}
