package com.gaog.weblog.common.aspect;

import java.lang.annotation.*;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 14:07
 * @Version: 1.0
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ApiOperationLog {
    /**
     * Api功能描述
     */
    String description() default "";
}
