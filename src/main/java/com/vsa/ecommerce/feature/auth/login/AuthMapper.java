package com.vsa.ecommerce.feature.auth.login;

import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.common.security.UserPrincipal;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthMapper {

    public LoginResponse.UserInfo toUserInfo(User user) {
        if (user == null) {
            return null;
        }

        return new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()),
                user.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(p -> p.getName())
                        .collect(Collectors.toSet()));
    }

    public LoginResponse.UserInfo toUserInfo(UserPrincipal principal) {
        if (principal == null) {
            return null;
        }

        return new LoginResponse.UserInfo(
                principal.getId(),
                principal.getEmail(),
                principal.getFirstName(),
                principal.getLastName(),
                principal.getRoleNames(),
                principal.getPermissionNames());
    }
}
