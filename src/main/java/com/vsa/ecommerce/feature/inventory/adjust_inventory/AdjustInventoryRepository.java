package com.vsa.ecommerce.feature.inventory.adjust_inventory;

import com.vsa.ecommerce.domain.entity.Inventory;
import com.vsa.ecommerce.domain.entity.InventoryTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureAdjustInventoryRepository")
public class AdjustInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Inventory> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Inventory.class, id));
    }

    public void save(Inventory inventory) {
        entityManager.merge(inventory);
    }

    public void saveTransaction(InventoryTransaction transaction) {
        entityManager.persist(transaction);
    }
}
