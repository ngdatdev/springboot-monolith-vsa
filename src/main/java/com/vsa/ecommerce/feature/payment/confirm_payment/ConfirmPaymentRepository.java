package com.vsa.ecommerce.feature.payment.confirm_payment;

import com.vsa.ecommerce.domain.entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureConfirmPaymentRepository")
public class ConfirmPaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Payment> findByTransactionId(String transactionId) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT p FROM Payment p WHERE p.transactionId = :transactionId", Payment.class)
                    .setParameter("transactionId", transactionId)
                    .getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }

    public void save(Payment payment) {
        entityManager.merge(payment);
    }
}
