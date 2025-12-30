package com.vsa.ecommerce.feature.auth.me;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.auth.login.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeResponse implements Response {
    private LoginResponse.UserInfo user;
}
