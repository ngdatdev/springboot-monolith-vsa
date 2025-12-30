package com.vsa.ecommerce.feature.auth.change_password;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordResponse implements Response {
    private String message;
}
