package com.vsa.ecommerce.feature.user.list_users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User item for list users response.
 * Each feature has its own response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserItem {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private Boolean active;
    private Boolean accountNonLocked;
}
