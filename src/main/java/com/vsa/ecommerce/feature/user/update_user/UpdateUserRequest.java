package com.vsa.ecommerce.feature.user.update_user;

import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    private String phoneNumber;
}
