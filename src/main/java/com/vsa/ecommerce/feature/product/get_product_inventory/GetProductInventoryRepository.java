package com.vsa.ecommerce.feature.product.get_product_inventory;

import com.vsa.ecommerce.domain.entity.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureGetProductInventoryRepository")
public class GetProductInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Inventory> findByProductId(Long productId) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT i FROM Inventory i WHERE i.product.id = :productId", Inventory.class)
                    .setParameter("productId", productId)
                    .getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }
}
