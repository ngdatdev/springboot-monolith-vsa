package com.vsa.ecommerce.feature.auth.register;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.mail.MailService;
import com.vsa.ecommerce.common.otp.OtpService;
import com.vsa.ecommerce.domain.entity.Role;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.domain.enums.UserRole;
import com.vsa.ecommerce.domain.enums.UserStatus;
import com.vsa.ecommerce.feature.auth.login.AuthMapper;
import com.vsa.ecommerce.feature.auth.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service for user registration.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class RegisterService implements IService<RegisterRequest, RegisterResponse> {

    private final com.vsa.ecommerce.common.security.repository.SecurityUserRepository userRepository;
    private final com.vsa.ecommerce.common.security.repository.SecurityRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final OtpService otpService;
    private final MailService mailService;

    @Override
    @Transactional
    public RegisterResponse execute(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new BusinessException(BusinessStatus.CONFLICT, "Email is already in use");
        }

        // 2. Get default USER role
        Role userRole = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new BusinessException(BusinessStatus.INTERNAL_SERVER_ERROR,
                        "Default USER role not found"));

        // 3. Create new User entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStatus(UserStatus.PENDING);
        user.setRoles(Set.of(userRole));

        // Account status fields
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);

        // 4. Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}. Status set to PENDING.", savedUser.getId());

        // 5. Send verification OTP
        String otp = otpService.generateOtp(savedUser.getEmail());
        mailService.sendEmailVerificationOtp(savedUser.getEmail(), otp);
        log.info("Verification OTP sent to: {}", savedUser.getEmail());

        // 6. Build response (UserInfo)
        LoginResponse.UserInfo userInfo = authMapper.toUserInfo(savedUser);

        return new RegisterResponse(userInfo);
    }
}
