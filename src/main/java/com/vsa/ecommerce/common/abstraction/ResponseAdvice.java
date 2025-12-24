package com.vsa.ecommerce.common.abstraction;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.server.ServletServerHttpResponse;

import java.util.Collections;
import java.util.Map;

/**
 * Global response advisor to automatically wrap successful API responses into
 * the Result envelope.
 * Ensures unified response format across the application.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply to all responses by default, or filter by package/annotation if needed
        return true;
    }

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof Result) {
            return body;
        }

        int status = 200;
        if (response instanceof ServletServerHttpResponse) {
            status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
        }

        if (status >= 400) {
            String errorMessage = "Error " + status;
            if (body instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) body;
                Object msg = map.get("message");
                if (msg != null) {
                    errorMessage = msg.toString();
                } else {
                    Object error = map.get("error");
                    if (error != null) {
                        errorMessage = error.toString();
                    }
                }
            } else if (body instanceof String) {
                errorMessage = (String) body;
            }
            return Result.failure(Collections.singletonList(errorMessage));
        }

        Result<Object> result = Result.success(body);

        if (body instanceof String) {
            return objectMapper.writeValueAsString(result);
        }

        return result;
    }
}
