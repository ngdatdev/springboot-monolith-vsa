package com.vsa.ecommerce.feature.auth.resend_verification;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.mail.MailService;
import com.vsa.ecommerce.common.otp.OtpService;
import com.vsa.ecommerce.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResendVerificationService implements IService<ResendVerificationRequest, EmptyResponse> {

    private final ResendVerificationRepository repository;
    private final OtpService otpService;
    private final MailService mailService;

    @Override
    @Transactional
    public EmptyResponse execute(ResendVerificationRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(BusinessStatus.USER_ALREADY_VERIFIED);
        }

        String otp = otpService.generateOtp(request.getEmail());
        mailService.sendEmailVerificationOtp(request.getEmail(), otp);
        log.info("Verification OTP sent to: {}", request.getEmail());

        return new EmptyResponse();
    }
}
