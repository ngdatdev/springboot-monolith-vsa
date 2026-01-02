package com.vsa.ecommerce.feature.inventory.list_inventory;

import com.vsa.ecommerce.domain.entity.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("FeatureListInventoryRepository")
public class ListInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Inventory> findAll(int page, int size) {
        return entityManager
                .createQuery("SELECT i FROM Inventory i JOIN FETCH i.product ORDER BY i.product.name", Inventory.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count() {
        return entityManager.createQuery("SELECT COUNT(i) FROM Inventory i", Long.class).getSingleResult();
    }
}
