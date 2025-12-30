package com.vsa.ecommerce.feature.auth.register;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.auth.login.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Register response DTO.
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterResponse implements Response {
    private LoginResponse.UserInfo user;
}
