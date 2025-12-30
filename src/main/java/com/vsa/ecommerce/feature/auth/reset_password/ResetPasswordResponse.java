package com.vsa.ecommerce.feature.auth.reset_password;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordResponse implements Response {
    private String message;
}
