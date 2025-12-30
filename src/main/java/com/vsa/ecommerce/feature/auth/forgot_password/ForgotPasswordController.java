package com.vsa.ecommerce.feature.auth.forgot_password;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.ratelimit.RateLimit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for forgot password requests.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController extends BaseController {

    private final ForgotPasswordService forgotPasswordService;

    @RateLimit(maxRequests = 3)
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> handle(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = forgotPasswordService.execute(request);
        return ResponseEntity.ok(response);
    }
}
