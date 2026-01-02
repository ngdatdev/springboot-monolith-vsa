package com.vsa.ecommerce.feature.auth.logout;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutRequest implements Request {
    private String token; // Access token or refresh token? Usually Refresh Token is mostly blacklisted,
                          // access token short lived.
                          // But for security, we assume blacklisting the token passed (which might be
                          // access token extracted from header by controller).
}
