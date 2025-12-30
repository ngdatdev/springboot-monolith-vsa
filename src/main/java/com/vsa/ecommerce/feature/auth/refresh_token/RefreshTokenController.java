package com.vsa.ecommerce.feature.auth.refresh_token;

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
 * Controller for token refresh operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints")
@Slf4j
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Refresh access token", description = "Renew an expired access token using a refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> handle(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = refreshTokenService.execute(request);
        return ResponseEntity.ok(response);
    }
}
