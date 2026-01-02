package com.vsa.ecommerce.feature.payment.confirm_payment;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.domain.entity.Payment;
import com.vsa.ecommerce.domain.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConfirmPaymentService implements Service<ConfirmPaymentRequest, EmptyResponse> {

    private final ConfirmPaymentRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(ConfirmPaymentRequest request) {
        Payment payment = repository.findByTransactionId(request.getTransactionId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            // Idempotency: If already confirmed, just return success
            if ((request.isSuccess() && payment.getStatus() == PaymentStatus.CAPTURED) ||
                    (!request.isSuccess() && payment.getStatus() == PaymentStatus.FAILED)) {
                return new EmptyResponse();
            }
            throw new IllegalStateException("Payment status cannot be updated from " + payment.getStatus());
        }

        Order order = payment.getOrder();
        if (request.isSuccess()) {
            payment.setStatus(PaymentStatus.CAPTURED);
            order.markPaid(); // Updates Order Status to CONFIRMED
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            // order status remains PENDING_PAYMENT or moves to PAYMENT_FAILED
        }

        repository.save(payment);
        return new EmptyResponse();
    }
}
