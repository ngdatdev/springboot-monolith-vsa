package com.vsa.ecommerce.feature.auth.login;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.UserPrincipal;
import com.vsa.ecommerce.common.security.jwt.JwtProperties;
import com.vsa.ecommerce.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Authentication Service for login/logout operations.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class LoginService implements Service<LoginRequest, LoginResponse> {

        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final JwtProperties jwtProperties;
        private final AuthMapper authMapper;
        private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;
        private final com.vsa.ecommerce.common.security.LoginAttemptService loginAttemptService;

        @Override
        public LoginResponse execute(LoginRequest request) {
                // Check if account is blocked
                if (loginAttemptService.isBlocked(request.getEmail())) {
                        log.warn("Login blocked for email: {} due to too many failed attempts", request.getEmail());
                        throw new BusinessException(BusinessStatus.TOO_MANY_REQUESTS,
                                        "Account is temporarily locked. Please try again later.");
                }

                try {
                        // Authenticate user with email and password
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));

                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Success: Reset attempt count
                        loginAttemptService.loginSucceeded(request.getEmail());

                        // Get authenticated user
                        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                        // Generate JWT access token
                        String accessToken = jwtTokenProvider.generateAccessToken(
                                        userPrincipal.getId(),
                                        userPrincipal.getEmail(),
                                        userPrincipal.getAuthorities());

                        // Generate JWT refresh token
                        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal.getId());

                        // Store refresh token in Redis
                        String redisKey = "refresh_token:" + userPrincipal.getId();
                        redisTemplate.opsForValue().set(
                                        redisKey,
                                        refreshToken,
                                        jwtProperties.getRefreshExpirationMs(),
                                        java.util.concurrent.TimeUnit.MILLISECONDS);

                        // Calculate token expirations
                        Long expiresIn = jwtProperties.getExpirationMs() / 1000;
                        Long refreshExpiresIn = jwtProperties.getRefreshExpirationMs() / 1000;

                        // Build user info
                        LoginResponse.UserInfo userInfo = authMapper.toUserInfo(userPrincipal);

                        log.info("Login successful for user: {}", request.getEmail());

                        return new LoginResponse(accessToken, refreshToken, expiresIn, refreshExpiresIn, userInfo);

                } catch (org.springframework.security.core.AuthenticationException ex) {
                        // Failure: Increment attempt count
                        loginAttemptService.loginFailed(request.getEmail());
                        throw new BusinessException(BusinessStatus.UNAUTHORIZED, "Invalid email or password");
                }
        }
}
