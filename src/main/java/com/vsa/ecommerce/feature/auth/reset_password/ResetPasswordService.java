package com.vsa.ecommerce.feature.auth.reset_password;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.otp.OtpService;
import com.vsa.ecommerce.domain.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to handle reset password requests.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ResetPasswordService implements Service<ResetPasswordRequest, ResetPasswordResponse> {

    private final ResetPasswordRepository repository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResetPasswordResponse execute(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(BusinessStatus.PARAM_ERROR, "New password and confirm password do not match");
        }

        // Validate token
        boolean isValidToken = otpService.validateOtp(request.getEmail(), request.getToken());
        if (!isValidToken) {
            log.warn("Invalid or expired reset token for email: {}", request.getEmail());
            throw new BusinessException(BusinessStatus.UNAUTHORIZED, "Invalid or expired reset token");
        }

        // Get user and update password
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);

        log.info("Password reset successfully for email: {}", request.getEmail());

        return new ResetPasswordResponse("Password has been reset successfully.");
    }
}
