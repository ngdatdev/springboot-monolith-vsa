package com.vsa.ecommerce.feature.order.admin_update_status;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateOrderStatusService implements Service<UpdateOrderStatusRequest, EmptyResponse> {

    private final UpdateOrderStatusRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(UpdateOrderStatusRequest request) {
        Order order = repository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.ORDER_NOT_FOUND));

        // Use specific domain methods for transitions to ensure invariants
        switch (request.getNewStatus()) {
            case CONFIRMED:
                order.markPaid();
                break;
            case PROCESSING:
                order.markProcessing();
                break;
            case READY_TO_SHIP:
                order.markReadyToShip();
                break;
            case DELIVERED:
                order.markDelivered();
                break;
            case COMPLETED:
                order.complete();
                break;
            default:
                // For other statuses or generic updates, we might set directly if allowed, or
                // throw error
                // e.g. SHIPPED needs tracking info, CANCELLED needs reason
                throw new BusinessException(BusinessStatus.INVALID_ORDER_STATUS_TRANSITION);
        }

        repository.save(order);
        return new EmptyResponse();
    }
}
