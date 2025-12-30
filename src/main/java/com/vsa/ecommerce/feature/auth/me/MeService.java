package com.vsa.ecommerce.feature.auth.me;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.feature.auth.login.AuthMapper;
import com.vsa.ecommerce.feature.auth.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to get current user profile.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MeService implements Service<MeRequest, MeResponse> {

        private final MeRepository meRepository;
        private final AuthMapper authMapper;

        @Override
        @Transactional(readOnly = true)
        public MeResponse execute(MeRequest request) {
                Long currentUserId = SecurityUtils.getCurrentUserId()
                                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

                User user = meRepository.findById(currentUserId)
                                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND, "User not found"));

                log.info("Successfully retrieved profile for user: {} (ID: {})", user.getEmail(), currentUserId);

                LoginResponse.UserInfo userInfo = authMapper.toUserInfo(user);

                return new MeResponse(userInfo);
        }
}
