package com.vsa.ecommerce.feature.inventory.get_inventory_transactions;

import com.vsa.ecommerce.domain.entity.InventoryTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("FeatureGetInventoryTransactionsRepository")
public class GetInventoryTransactionsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<InventoryTransaction> findByInventoryId(Long inventoryId) {
        return entityManager.createQuery(
                "SELECT t FROM InventoryTransaction t WHERE t.inventory.id = :inventoryId ORDER BY t.createdAt DESC",
                InventoryTransaction.class)
                .setParameter("inventoryId", inventoryId)
                .getResultList();
    }
}
