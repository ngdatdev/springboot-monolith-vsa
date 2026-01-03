package com.vsa.ecommerce.feature.auth.change_password;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to change user password.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ChangePasswordService implements IService<ChangePasswordRequest, ChangePasswordResponse> {

    private final ChangePasswordRepository changePasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ChangePasswordResponse execute(ChangePasswordRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password change failed for user ID: {}: New passwords do not match", currentUserId);
            throw new BusinessException(BusinessStatus.PARAM_ERROR, "New password and confirm password do not match");
        }

        User user = changePasswordRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed for user: {}: Incorrect old password", user.getEmail());
            throw new BusinessException(BusinessStatus.PARAM_ERROR, "Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        changePasswordRepository.save(user);

        log.info("Password changed successfully for user: {} (ID: {})", user.getEmail(), currentUserId);

        return new ChangePasswordResponse("Password changed successfully");
    }
}
