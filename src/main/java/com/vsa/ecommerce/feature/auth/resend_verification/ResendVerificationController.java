package com.vsa.ecommerce.feature.auth.resend_verification;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller for resend verification requests.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints")
public class ResendVerificationController extends BaseController {

    private final ResendVerificationService service;

    @PostMapping("/resend-verification")
    public ResponseEntity<EmptyResponse> resendVerification(@RequestBody ResendVerificationRequest request) {
        return ResponseEntity.ok(service.execute(request));
    }
}
