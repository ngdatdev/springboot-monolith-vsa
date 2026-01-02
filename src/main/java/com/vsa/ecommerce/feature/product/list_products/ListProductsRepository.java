package com.vsa.ecommerce.feature.product.list_products;

import com.vsa.ecommerce.domain.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("FeatureListProductsRepository")
public class ListProductsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> findAll(String search, int page, int size) {
        String jpql = "SELECT p FROM Product p";
        if (search != null && !search.isEmpty()) {
            jpql += " WHERE LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search";
        }
        jpql += " ORDER BY p.name ASC";

        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        if (search != null && !search.isEmpty()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }

        return query.setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count(String search) {
        String jpql = "SELECT COUNT(p) FROM Product p";
        if (search != null && !search.isEmpty()) {
            jpql += " WHERE LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        if (search != null && !search.isEmpty()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }
        return query.getSingleResult();
    }
}
