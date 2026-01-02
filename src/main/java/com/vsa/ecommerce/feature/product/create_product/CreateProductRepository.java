package com.vsa.ecommerce.feature.product.create_product;

import com.vsa.ecommerce.domain.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository("FeatureCreateProductRepository")
public class CreateProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Product product) {
        entityManager.persist(product);
    }
}
