package com.vsa.ecommerce.feature.payment.get_order_payments;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.domain.entity.Payment;
import com.vsa.ecommerce.feature.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetOrderPaymentsService implements IService<GetOrderPaymentsRequest, GetOrderPaymentsResponse> {

    private final GetOrderPaymentsRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetOrderPaymentsResponse execute(GetOrderPaymentsRequest request) {
        List<Payment> payments = repository.findByOrderId(request.getOrderId());

        List<PaymentDto> dtos = payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return GetOrderPaymentsResponse.builder().payments(dtos).build();
    }

    private PaymentDto mapToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
