package com.vsa.ecommerce.feature.user.update_user_status;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class UserStatusRequest {
    @NotNull
    private Boolean active;
}
