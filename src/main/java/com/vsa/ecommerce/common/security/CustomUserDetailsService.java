package com.vsa.ecommerce.common.security;

import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService for loading user from database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final com.vsa.ecommerce.common.security.repository.SecurityUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND, "User not found with id: " + id));

        return UserPrincipal.create(user);
    }
}
