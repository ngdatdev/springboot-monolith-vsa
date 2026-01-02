package com.vsa.ecommerce.feature.product.get_product;

import com.vsa.ecommerce.domain.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureGetProductRepository")
public class GetProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }
}
