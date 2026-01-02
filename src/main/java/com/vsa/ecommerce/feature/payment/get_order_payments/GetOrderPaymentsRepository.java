package com.vsa.ecommerce.feature.payment.get_order_payments;

import com.vsa.ecommerce.domain.entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("FeatureGetOrderPaymentsRepository")
public class GetOrderPaymentsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Payment> findByOrderId(Long orderId) {
        return entityManager
                .createQuery("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.createdAt DESC",
                        Payment.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
