package com.vsa.ecommerce.feature.auth.login;

import com.vsa.ecommerce.common.abstraction.Service;
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

        @Override
        public LoginResponse execute(LoginRequest request) {
                log.info("Login attempt for email: {}", request.getEmail());

                // Authenticate user with email and password
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Get authenticated user
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                // Generate JWT token
                String accessToken = jwtTokenProvider.generateAccessToken(
                                userPrincipal.getId(),
                                userPrincipal.getEmail(),
                                userPrincipal.getAuthorities());

                // Calculate token expiration
                Long expiresIn = jwtProperties.getExpirationMs() / 1000; // Convert to seconds

                // Build user info
                LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                                userPrincipal.getId(),
                                userPrincipal.getEmail(),
                                userPrincipal.getFirstName(),
                                userPrincipal.getLastName(),
                                userPrincipal.getRoleNames(),
                                userPrincipal.getPermissionNames());

                log.info("Login successful for user: {}", request.getEmail());

                return new LoginResponse(accessToken, expiresIn, userInfo);
        }
}
