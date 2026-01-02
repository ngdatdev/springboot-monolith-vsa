package com.vsa.ecommerce.feature.auth.me;

import com.vsa.ecommerce.common.abstraction.BaseController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving current user profile.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Auth endpoints")
@RequiredArgsConstructor
public class MeController extends BaseController {

    private final MeService meService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> handle() {
        MeResponse response = meService.execute(new MeRequest());
        return ResponseEntity.ok(response);
    }
}
