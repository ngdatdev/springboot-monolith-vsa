package com.vsa.ecommerce.feature.auth.logout;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.security.jwt.BlacklistService;
import com.vsa.ecommerce.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class LogoutService implements IService<LogoutRequest, EmptyResponse> {

    private final BlacklistService blacklistService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public EmptyResponse execute(LogoutRequest request) {
        String token = request.getToken();

        try {
            // Get actual expiration date from token
            Date expiration = jwtTokenProvider.getExpirationDate(token);
            blacklistService.blacklistToken(token, expiration);
        } catch (Exception e) {
            // If token is already invalid/expired, we don't need to blacklist it
            // But we can still log it or just ignore
        }

        return new EmptyResponse();
    }
}
