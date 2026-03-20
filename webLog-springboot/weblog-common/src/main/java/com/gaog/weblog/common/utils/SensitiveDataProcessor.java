package com.gaog.weblog.common.utils;

import com.gaog.weblog.common.annotation.SensitiveField;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 遍历对象树，对标注字段执行传输层加解密
 */
@Component
public class SensitiveDataProcessor {

    private final TransportCryptoUtils transportCryptoUtils;

    public SensitiveDataProcessor(TransportCryptoUtils transportCryptoUtils) {
        this.transportCryptoUtils = transportCryptoUtils;
    }

    public void decryptRequestFields(Object target) {
        process(target, Mode.REQUEST, new IdentityHashMap<>());
    }

    public void encryptResponseFields(Object target) {
        process(target, Mode.RESPONSE, new IdentityHashMap<>());
    }

    private void process(Object target, Mode mode, IdentityHashMap<Object, Boolean> visited) {
        if (target == null || visited.containsKey(target) || isTerminalType(target.getClass())) {
            return;
        }

        visited.put(target, Boolean.TRUE);

        if (target instanceof Collection) {
            for (Object item : (Collection<?>) target) {
                process(item, mode, visited);
            }
            return;
        }

        if (target instanceof Map) {
            for (Object value : ((Map<?, ?>) target).values()) {
                process(value, mode, visited);
            }
            return;
        }

        if (target.getClass().isArray()) {
            int length = Array.getLength(target);
            for (int i = 0; i < length; i++) {
                process(Array.get(target, i), mode, visited);
            }
            return;
        }

        ReflectionUtils.doWithFields(target.getClass(), field -> processField(target, field, mode, visited));
    }

    private void processField(Object target, Field field, Mode mode, IdentityHashMap<Object, Boolean> visited) throws IllegalAccessException {
        ReflectionUtils.makeAccessible(field);
        Object value = field.get(target);
        if (value == null) {
            return;
        }

        SensitiveField sensitiveField = field.getAnnotation(SensitiveField.class);
        if (sensitiveField != null && field.getType() == String.class) {
            if (mode == Mode.REQUEST && sensitiveField.request()) {
                field.set(target, transportCryptoUtils.decryptIfNecessary((String) value));
            }
            if (mode == Mode.RESPONSE && sensitiveField.response()) {
                field.set(target, transportCryptoUtils.encryptIfNecessary((String) value));
            }
            return;
        }

        process(value, mode, visited);
    }

    private boolean isTerminalType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.isEnum()
                || Number.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Boolean.class == clazz
                || Character.class == clazz
                || java.util.Date.class.isAssignableFrom(clazz)
                || clazz.getName().startsWith("java.time.")
                || clazz.getName().startsWith("java.");
    }

    private enum Mode {
        REQUEST,
        RESPONSE
    }
}
