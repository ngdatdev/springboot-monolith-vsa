package com.vsa.ecommerce.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService for loading user from database.
 * 
 * TODO: Integrate with cache system for performance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // TODO: Inject UserRepository when ready
    // private final UserRepository userRepository;
    // private final HybridCacheManager cacheManager;
    // private final CacheKeyConvention keyConvention;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        // TODO: Implement with cache
        // String cacheKey = keyConvention.buildKey(RESOURCE_USER, email);
        // User user = cacheManager.getOrCompute(cacheKey, User.class,
        // () -> userRepository.findByEmail(email)
        // .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
        // );

        // Temporary: throw exception until repository is ready
        throw new UsernameNotFoundException("User not found: " + email + " (UserRepository not implemented)");

        // When ready, uncomment:
        // return UserPrincipal.create(user);
    }

    /**
     * Load user by ID (used after JWT validation).
     */
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user by ID: {}", id);

        // TODO: Implement with cache
        // String cacheKey = keyConvention.buildKey(RESOURCE_USER, id.toString());
        // User user = cacheManager.getOrCompute(cacheKey, User.class,
        // () -> userRepository.findById(id)
        // .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " +
        // id))
        // );

        throw new UsernameNotFoundException("User not found with id: " + id + " (UserRepository not implemented)");

        // return UserPrincipal.create(user);
    }
}
