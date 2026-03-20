package com.gaog.weblog.common.advice;

import com.gaog.weblog.common.utils.SensitiveDataProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * 自动解密请求体中的敏感字段
 */
@ControllerAdvice
public class SensitiveRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private final SensitiveDataProcessor sensitiveDataProcessor;

    public SensitiveRequestBodyAdvice(SensitiveDataProcessor sensitiveDataProcessor) {
        this.sensitiveDataProcessor = sensitiveDataProcessor;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        if (inputMessage instanceof ServletServerHttpRequest) {
            sensitiveDataProcessor.decryptRequestFields(body);
        }
        return body;
    }
}
