package com.vsa.ecommerce.feature.auth.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.ratelimit.RateLimit;

import jakarta.validation.Valid;

/**
 * Authentication Controller.
 * 
 * Endpoints:
 * - POST /api/auth/login - Login with email/password
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController extends BaseController {

    private final LoginService loginService;

    /**
     * Login endpoint.
     * 
     * @param request Login credentials (email + password)
     * @return JWT token and user information
     */
    @RateLimit(maxRequests = 5)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> handle(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.execute(request);
        return ResponseEntity.ok(response);
    }
}
