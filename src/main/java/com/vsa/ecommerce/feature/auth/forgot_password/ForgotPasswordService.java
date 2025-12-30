package com.vsa.ecommerce.feature.auth.forgot_password;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.mail.MailService;
import com.vsa.ecommerce.common.otp.OtpService;
import com.vsa.ecommerce.feature.auth.common.AuthProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to handle forgot password requests.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ForgotPasswordService implements Service<ForgotPasswordRequest, ForgotPasswordResponse> {

    private final ForgotPasswordRepository repository;
    private final OtpService otpService;
    private final MailService mailService;
    private final AuthProperties authProperties;

    @Override
    public ForgotPasswordResponse execute(ForgotPasswordRequest request) {
        if (!repository.existsByEmail(request.getEmail())) {
            // We return success message even if email doesn't exist for security reasons
            log.warn("Forgot password request for non-existent email: {}", request.getEmail());
            return new ForgotPasswordResponse(
                    "If your email is registered, you will receive reset instructions shortly.");
        }

        // Generate reset token (OTP)
        String resetToken = otpService.generateOtp(request.getEmail());

        // Send email
        String resetLink = String.format("%s?token=%s&email=%s",
                authProperties.getPasswordResetUrl(), resetToken, request.getEmail());
        mailService.sendPasswordResetEmail(request.getEmail(), resetLink);

        log.info("Password reset email sent to: {}", request.getEmail());

        return new ForgotPasswordResponse("Reset password instructions have been sent to your email.");
    }
}
