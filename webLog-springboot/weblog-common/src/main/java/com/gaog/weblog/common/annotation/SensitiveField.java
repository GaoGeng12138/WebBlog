package com.gaog.weblog.common.annotation;

import java.lang.annotation.*;

/**
 * 标记需要在传输层加解密的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveField {
    boolean request() default true;

    boolean response() default true;
}
