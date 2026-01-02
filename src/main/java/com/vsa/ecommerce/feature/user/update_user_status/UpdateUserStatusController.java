package com.vsa.ecommerce.feature.user.update_user_status;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.user.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
public class UpdateUserStatusController extends BaseController {

    private final UpdateUserStatusService updateUserStatusService;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable Long id, @RequestBody UserStatusRequest request) {
        return ResponseEntity.ok(updateUserStatusService
                .execute(UpdateUserStatusService.Request.builder().id(id).statusRequest(request).build()));
    }
}
