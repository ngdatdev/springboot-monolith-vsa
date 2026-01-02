package com.vsa.ecommerce.feature.auth.reset_password;

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
 * Controller for reset password requests.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Auth endpoints")
@RequiredArgsConstructor
public class ResetPasswordController extends BaseController {

    private final ResetPasswordService resetPasswordService;

    @RateLimit(maxRequests = 5)
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> handle(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = resetPasswordService.execute(request);
        return ResponseEntity.ok(response);
    }
}
