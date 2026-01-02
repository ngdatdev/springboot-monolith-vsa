package com.vsa.ecommerce.feature.user.update_user;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.feature.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateUserService implements Service<UpdateUserService.Request, UserDto> {

    private final UpdateUserRepository updateUserRepository;

    @lombok.Data
    @lombok.Builder
    public static class Request implements com.vsa.ecommerce.common.abstraction.Request {
        private Long id;
        private UpdateUserRequest updateRequest;
    }

    @Override
    @Transactional
    public UserDto execute(Request request) {
        User user = updateUserRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        UpdateUserRequest updateRequest = request.getUpdateRequest();
        if (updateRequest.getFirstName() != null)
            user.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null)
            user.setLastName(updateRequest.getLastName());
        if (updateRequest.getPhoneNumber() != null)
            user.setPhoneNumber(updateRequest.getPhoneNumber());

        updateUserRepository.save(user);
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
