package com.vsa.ecommerce.feature.payment.initiate_payment;

import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.domain.entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureInitiatePaymentRepository")
public class InitiatePaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Order> findOrderById(Long id) {
        return Optional.ofNullable(entityManager.find(Order.class, id));
    }

    public void save(Payment payment) {
        entityManager.persist(payment);
    }
}
