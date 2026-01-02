package com.vsa.ecommerce.feature.cart.update_cart_item;

import com.vsa.ecommerce.common.abstraction.Request;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequest implements Request {

    @JsonIgnore
    private Long itemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
