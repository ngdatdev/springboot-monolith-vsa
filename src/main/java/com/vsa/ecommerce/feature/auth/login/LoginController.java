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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/*
 * Controller for login requests.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Auth endpoints")
@RequiredArgsConstructor
public class LoginController extends BaseController {

    private final LoginService loginService;

    @RateLimit(maxRequests = 5)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> handle(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.execute(request);
        return ResponseEntity.ok(response);
    }
}
