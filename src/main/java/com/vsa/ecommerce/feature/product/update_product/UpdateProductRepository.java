package com.vsa.ecommerce.feature.product.update_product;

import com.vsa.ecommerce.domain.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureUpdateProductRepository")
public class UpdateProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    public void save(Product product) {
        entityManager.merge(product);
    }
}
