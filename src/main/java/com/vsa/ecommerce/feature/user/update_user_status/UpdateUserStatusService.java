package com.vsa.ecommerce.feature.user.update_user_status;

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
public class UpdateUserStatusService implements Service<UpdateUserStatusService.Request, UserDto> {

    private final UpdateUserStatusRepository updateUserStatusRepository;

    @lombok.Data
    @lombok.Builder
    public static class Request implements com.vsa.ecommerce.common.abstraction.Request {
        private Long id;
        private UserStatusRequest statusRequest;
    }

    @Override
    @Transactional
    public UserDto execute(Request request) {
        User user = updateUserStatusRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        user.setEnabled(request.getStatusRequest().getActive());
        updateUserStatusRepository.save(user);
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
