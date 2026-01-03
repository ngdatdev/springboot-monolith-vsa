package com.vsa.ecommerce.common.abstraction;

import com.vsa.ecommerce.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the application.
 * Captures BusinessException and other exceptions, converting them into a
 * standardized Result format.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException exception) {
        log.error("Business Exception: {}", exception.getMessage());
        int httpStatus = HttpStatusMapper.mapToStatus(exception.getStatus()).value();

        return ResponseEntity
                .status(httpStatus)
                .body(Result.failure(Collections.singletonList(exception.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception exception) {
        log.error("Unhandled Exception occurred: ", exception);
        return ResponseEntity
                .internalServerError()
                .body(Result.failure(
                        Collections.singletonList(
                                "Internal Server Error: " + exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.failure(errors));
    }
}
