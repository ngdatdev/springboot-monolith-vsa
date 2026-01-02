package com.vsa.ecommerce.feature.auth.change_password;

import com.vsa.ecommerce.common.abstraction.BaseController;

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
 * Controller for changing user password.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Auth endpoints")
@RequiredArgsConstructor
public class ChangePasswordController extends BaseController {

    private final ChangePasswordService changePasswordService;

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> handle(@Valid @RequestBody ChangePasswordRequest request) {
        ChangePasswordResponse response = changePasswordService.execute(request);
        return ResponseEntity.ok(response);
    }
}
