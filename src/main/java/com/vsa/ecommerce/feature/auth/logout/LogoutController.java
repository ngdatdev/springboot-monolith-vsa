package com.vsa.ecommerce.feature.auth.logout;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller for logout requests.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints")
public class LogoutController extends BaseController {

    private final LogoutService service;

    @PostMapping("/logout")
    public ResponseEntity<EmptyResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            service.execute(LogoutRequest.builder().token(token).build());
        }
        return ResponseEntity.ok(new EmptyResponse());
    }
}
