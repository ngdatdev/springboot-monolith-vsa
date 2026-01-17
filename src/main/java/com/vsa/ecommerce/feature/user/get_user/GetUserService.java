package com.vsa.ecommerce.feature.user.get_user;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetUserService implements IService<GetUserRequest, GetUserResponse> {

    private final GetUserRepository getUserRepository;

    @Override
    @Transactional(readOnly = true)
    public GetUserResponse execute(GetUserRequest request) {
        User user = getUserRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));
        return mapToResponse(user);
    }

    private GetUserResponse mapToResponse(User user) {
        return GetUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRoles().isEmpty() ? null : user.getRoles().iterator().next().getName().name())
                .active(user.getEnabled())
                .accountNonLocked(user.getAccountNonLocked())
                .build();
    }
}
