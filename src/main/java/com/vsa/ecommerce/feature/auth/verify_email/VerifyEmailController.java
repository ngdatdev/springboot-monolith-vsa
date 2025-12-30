package com.vsa.ecommerce.feature.auth.verify_email;

import io.swagger.v3.oas.annotations.Operation;
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
 * Controller for email verification.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints")
@Slf4j
public class VerifyEmailController {

    private final VerifyEmailService verifyEmailService;

    @Operation(summary = "Verify email", description = "Verify user email using the OTP sent during registration")
    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> handle(@Valid @RequestBody VerifyEmailRequest request) {
        VerifyEmailResponse response = verifyEmailService.execute(request);
        return ResponseEntity.ok(response);
    }
}
