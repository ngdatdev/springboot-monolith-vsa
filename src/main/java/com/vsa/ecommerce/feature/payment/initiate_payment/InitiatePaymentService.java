package com.vsa.ecommerce.feature.payment.initiate_payment;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.domain.entity.Payment;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import com.vsa.ecommerce.domain.enums.PaymentStatus;
import com.vsa.ecommerce.feature.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitiatePaymentService implements Service<InitiatePaymentRequest, PaymentDto> {

    private final InitiatePaymentRepository repository;

    @Override
    @Transactional
    public PaymentDto execute(InitiatePaymentRequest request) {
        Order order = repository.findOrderById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new IllegalStateException("Order is not eligible for payment. Status: " + order.getStatus());
        }

        // Mock Gateway Logic
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString()); // Mock Transaction ID

        repository.save(payment);

        // Update Order status to indicate processing (optional, depends on flow)
        // order.setStatus(OrderStatus.PAYMENT_PROCESSING);

        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(order.getId())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
