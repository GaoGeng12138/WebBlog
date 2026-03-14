package com.gaog.weblog.jwt.exception;


import org.springframework.security.core.AuthenticationException;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 22:44
 * @Version: 1.0
 * @Description:
 */
public class UsernameOrPasswordNullException extends AuthenticationException {
    private static final long serialVersionUID = -5540687081368968692L;
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}
