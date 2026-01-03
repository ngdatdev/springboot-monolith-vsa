package com.vsa.ecommerce.feature.user.list_users;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.feature.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListUsersService implements IService<ListUsersRequest, UserListResponse> {

    private final ListUsersRepository listUsersRepository;

    @Override
    @Transactional(readOnly = true)
    public UserListResponse execute(ListUsersRequest request) {
        List<UserDto> users = listUsersRepository.findAll(request.getPage(), request.getSize()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return UserListResponse.builder().users(users).build();
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
