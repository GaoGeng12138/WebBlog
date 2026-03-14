package com.gaog.weblog.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/16
 * @Version: 1.0
 * @Description: 账户未启用异常
 */
public class AccountDisabledException extends AuthenticationException {
    private static final long serialVersionUID = -5540687081368968693L;

    public AccountDisabledException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AccountDisabledException(String msg) {
        super(msg);
    }
}
