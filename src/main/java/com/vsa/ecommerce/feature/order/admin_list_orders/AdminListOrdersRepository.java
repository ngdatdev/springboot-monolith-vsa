package com.vsa.ecommerce.feature.order.admin_list_orders;

import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("FeatureAdminListOrdersRepository")
public class AdminListOrdersRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Order> findAll(OrderStatus status, int page, int size) {
        String jpql = "SELECT o FROM Order o";
        if (status != null) {
            jpql += " WHERE o.status = :status";
        }
        jpql += " ORDER BY o.createdAt DESC";

        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        if (status != null) {
            query.setParameter("status", status);
        }

        return query.setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count(OrderStatus status) {
        String jpql = "SELECT COUNT(o) FROM Order o";
        if (status != null) {
            jpql += " WHERE o.status = :status";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getSingleResult();
    }
}
