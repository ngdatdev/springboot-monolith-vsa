package com.vsa.ecommerce.feature.user.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto implements Response {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role; // Changed to String to simplify mapping from Set<Role>
    private Boolean active;
    private Boolean accountNonLocked;
}
