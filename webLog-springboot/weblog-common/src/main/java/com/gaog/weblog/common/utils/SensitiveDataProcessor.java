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

    public boolean decryptRequestFields(Object target) {
        return process(target, Mode.REQUEST, new IdentityHashMap<>());
    }

    public boolean encryptResponseFields(Object target) {
        return process(target, Mode.RESPONSE, new IdentityHashMap<>());
    }

    private boolean process(Object target, Mode mode, IdentityHashMap<Object, Boolean> visited) {
        if (target == null || visited.containsKey(target) || isTerminalType(target.getClass())) {
            return false;
        }

        visited.put(target, Boolean.TRUE);
        boolean changed = false;

        if (target instanceof Collection) {
            for (Object item : (Collection<?>) target) {
                changed = process(item, mode, visited) || changed;
            }
            return changed;
        }

        if (target instanceof Map) {
            for (Object value : ((Map<?, ?>) target).values()) {
                changed = process(value, mode, visited) || changed;
            }
            return changed;
        }

        if (target.getClass().isArray()) {
            int length = Array.getLength(target);
            for (int i = 0; i < length; i++) {
                changed = process(Array.get(target, i), mode, visited) || changed;
            }
            return changed;
        }

        final boolean[] fieldChanged = {false};
        ReflectionUtils.doWithFields(target.getClass(), field -> {
            if (processField(target, field, mode, visited)) {
                fieldChanged[0] = true;
            }
        });
        return changed || fieldChanged[0];
    }

    private boolean processField(Object target, Field field, Mode mode, IdentityHashMap<Object, Boolean> visited) throws IllegalAccessException {
        ReflectionUtils.makeAccessible(field);
        Object value = field.get(target);
        if (value == null) {
            return false;
        }

        SensitiveField sensitiveField = field.getAnnotation(SensitiveField.class);
        if (sensitiveField != null && field.getType() == String.class) {
            if (mode == Mode.REQUEST && sensitiveField.request()) {
                String decryptedValue = transportCryptoUtils.decryptIfNecessary((String) value);
                if (!decryptedValue.equals(value)) {
                    field.set(target, decryptedValue);
                    return true;
                }
            }
            if (mode == Mode.RESPONSE && sensitiveField.response()) {
                String encryptedValue = transportCryptoUtils.encryptIfNecessary((String) value);
                if (!encryptedValue.equals(value)) {
                    field.set(target, encryptedValue);
                    return true;
                }
            }
            return false;
        }

        return process(value, mode, visited);
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
