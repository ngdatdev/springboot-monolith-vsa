package com.vsa.ecommerce.feature.auth.register;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.ratelimit.RateLimit;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Register Controller.
 * 
 * Endpoints:
 * - POST /api/auth/register - Register a new user
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Auth endpoints")
@RequiredArgsConstructor
public class RegisterController extends BaseController {

    private final RegisterService registerService;

    /**
     * Register endpoint.
     * 
     * @param request Registration details
     * @return Registered user information
     */
    @RateLimit(maxRequests = 3) // More strict for registration
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> handle(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerService.execute(request);
        return ResponseEntity.ok(response);
    }
}
