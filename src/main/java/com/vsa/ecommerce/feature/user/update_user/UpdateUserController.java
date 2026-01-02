package com.vsa.ecommerce.feature.user.update_user;

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
public class UpdateUserController extends BaseController {

    private final UpdateUserService updateUserService;

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(
                updateUserService.execute(UpdateUserService.Request.builder().id(id).updateRequest(request).build()));
    }
}
