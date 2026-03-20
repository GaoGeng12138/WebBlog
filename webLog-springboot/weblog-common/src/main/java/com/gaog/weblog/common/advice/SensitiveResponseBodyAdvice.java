package com.gaog.weblog.common.advice;

import com.gaog.weblog.common.utils.SensitiveDataProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 自动加密响应体中的敏感字段
 */
@ControllerAdvice
public class SensitiveResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final SensitiveDataProcessor sensitiveDataProcessor;

    public SensitiveResponseBodyAdvice(SensitiveDataProcessor sensitiveDataProcessor) {
        this.sensitiveDataProcessor = sensitiveDataProcessor;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        sensitiveDataProcessor.encryptResponseFields(body);
        response.getHeaders().add("X-Transport-Encrypted", "true");
        return body;
    }
}
