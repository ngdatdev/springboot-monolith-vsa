package com.vsa.ecommerce.feature.auth.logout;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.domain.entity.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LogoutService implements Service<LogoutRequest, EmptyResponse> {

    private final LogoutRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(LogoutRequest request) {
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setToken(request.getToken());
        // Expiry should ideally be parsed from token, but for now we set it to tomorrow
        blacklist.setExpiryDate(LocalDateTime.now().plusDays(1));

        repository.save(blacklist);
        return new EmptyResponse();
    }
}
