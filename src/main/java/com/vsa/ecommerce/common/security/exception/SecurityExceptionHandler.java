package com.vsa.ecommerce.common.security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler for security-related exceptions.
 */
@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

        /**
         * Handle authentication failures (wrong email/password).
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
                log.warn("Authentication failed: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", 401,
                                                "error", "Unauthorized",
                                                "message", "Invalid email or password"));
        }

        /**
         * Handle user not found.
         */
        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
                log.warn("User not found: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", 401,
                                                "error", "Unauthorized",
                                                "message", "Invalid email or password"));
        }

        /**
         * Handle general authentication exceptions.
         */
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
                log.error("Authentication error: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", 401,
                                                "error", "Unauthorized",
                                                "message", "Authentication failed"));
        }

        /**
         * Handle JWT authentication exceptions.
         */
        @ExceptionHandler(JwtAuthenticationException.class)
        public ResponseEntity<Map<String, Object>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
                log.error("JWT authentication error: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", 401,
                                                "error", "Unauthorized",
                                                "message", ex.getMessage()));
        }
}
