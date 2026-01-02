package com.vsa.ecommerce.feature.cart.dto;

import com.vsa.ecommerce.domain.enums.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.vsa.ecommerce.common.abstraction.Response;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Response {
    private Long id;
    private BigDecimal totalAmount;
    private CartStatus status;
    private List<CartItemDto> items;
}
