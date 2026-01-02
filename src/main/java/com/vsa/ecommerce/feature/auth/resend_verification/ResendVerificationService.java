package com.vsa.ecommerce.feature.auth.resend_verification;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResendVerificationService implements Service<ResendVerificationRequest, EmptyResponse> {

    private final ResendVerificationRepository repository;
    // Helper to send email would be injected here (MailService)

    @Override
    @Transactional
    public EmptyResponse execute(ResendVerificationRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEnabled())) {
            // Already verified
            return new EmptyResponse();
        }

        // Logic to generate new token and send email
        // Mocking it here as we don't have full MailService integration in this slice
        // context

        return new EmptyResponse();
    }
}
