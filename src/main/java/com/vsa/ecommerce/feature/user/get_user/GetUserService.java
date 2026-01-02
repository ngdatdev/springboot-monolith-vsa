package com.vsa.ecommerce.feature.user.get_user;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.feature.user.dto.UserDto;
import com.vsa.ecommerce.feature.user.dto.UserIdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetUserService implements Service<UserIdRequest, UserDto> {

    private final GetUserRepository getUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDto execute(UserIdRequest request) {
        User user = getUserRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));
        return mapToDto(user);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
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
