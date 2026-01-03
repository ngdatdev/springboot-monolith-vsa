package com.vsa.ecommerce.feature.auth.refresh_token;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.UserPrincipal;
import com.vsa.ecommerce.common.security.jwt.JwtProperties;
import com.vsa.ecommerce.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.concurrent.TimeUnit;

/**
 * Service to refresh access token using a valid refresh token.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class RefreshTokenService implements IService<RefreshTokenRequest, RefreshTokenResponse> {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserDetailsService userDetailsService;

    @Override
    public RefreshTokenResponse execute(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. Validate JWT structure and signature
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(BusinessStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        // 2. Extract User ID
        Long userId = Long.valueOf(jwtTokenProvider.getSubject(refreshToken));

        // 3. Check Redis for token existence and match
        String redisKey = "refresh_token:" + userId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            log.warn("Refresh token mismatch or expired in Redis for user ID: {}", userId);
            throw new BusinessException(BusinessStatus.UNAUTHORIZED, "Refresh token is invalid or expired");
        }

        // 4. Load user details
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(
                jwtTokenProvider.getEmailFromToken(refreshToken));

        // Re-generate access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getAuthorities());

        // Optional: Rotate refresh token
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        redisTemplate.opsForValue().set(
                redisKey,
                newRefreshToken,
                jwtProperties.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS);

        Long expiresIn = jwtProperties.getExpirationMs() / 1000;
        Long refreshExpiresIn = jwtProperties.getRefreshExpirationMs() / 1000;

        log.info("Token refreshed successfully for user ID: {}", userId);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken, expiresIn, refreshExpiresIn);
    }
}
