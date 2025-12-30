package com.vsa.ecommerce.feature.auth.verify_email;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.otp.OtpService;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.domain.enums.UserStatus;
import com.vsa.ecommerce.feature.auth.register.RegisterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to handle email verification via OTP.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class VerifyEmailService implements Service<VerifyEmailRequest, VerifyEmailResponse> {

    private final RegisterRepository userRepository;
    private final OtpService otpService;

    @Override
    @Transactional
    public VerifyEmailResponse execute(VerifyEmailRequest request) {
        log.info("Email verification attempt for: {}", request.getEmail());

        // 1. Validate OTP
        boolean isValid = otpService.validateOtp(request.getEmail(), request.getCode());
        if (!isValid) {
            log.warn("Invalid or expired verification OTP for: {}", request.getEmail());
            throw new BusinessException(BusinessStatus.UNAUTHORIZED, "Invalid or expired verification code");
        }

        // 2. Get user and update status
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() != UserStatus.PENDING) {
            log.info("User {} is already verified or in status: {}", request.getEmail(), user.getStatus());
            return new VerifyEmailResponse("Email is already verified");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true); // Should enable the user here
        userRepository.save(user);

        log.info("Email verified successfully for: {}", request.getEmail());

        return new VerifyEmailResponse("Email verified successfully. You can now login.");
    }
}
