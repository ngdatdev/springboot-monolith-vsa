package com.vsa.ecommerce.feature.payment.get_order_payments;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.payment.dto.PaymentDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GetOrderPaymentsResponse implements Response {
    private List<PaymentDto> payments;
}
