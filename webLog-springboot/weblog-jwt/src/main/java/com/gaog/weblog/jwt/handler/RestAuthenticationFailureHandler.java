package com.gaog.weblog.jwt.handler;

import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.exception.AccountDisabledException;
import com.gaog.weblog.jwt.exception.UsernameOrPasswordNullException;
import com.gaog.weblog.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 22:50
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 认证失败处理方法
     * 当用户认证过程中出现异常时，此方法会被调用
     * 根据不同的异常类型返回不同的错误信息
     *
     * @param request   HttpServletRequest对象，包含请求信息
     * @param response  HttpServletResponse对象，用于返回响应
     * @param exception AuthenticationException对象，包含认证异常信息
     * @throws IOException      可能抛出的IO异常
     * @throws ServletException 可能抛出的Servlet异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 记录认证失败的警告日志
        log.warn("AuthenticationException: ", exception);
        // 判断异常类型是否为用户名或密码为空异常
        if (exception instanceof UsernameOrPasswordNullException) {
            // 用户名或密码为空
            // 使用ResultUtil工具类返回失败响应，包含异常信息
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
            return;
        } else if (exception instanceof AuthenticationServiceException) {
            ResultUtil.fail(response, Response.fail(ResponseCodeEnum.TRANSPORT_DECRYPT_FAILED.getErrorCode(), exception.getMessage()));
            return;
        } else if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof AccountDisabledException) {
                // 账户未启用，返回原始异常信息
                ResultUtil.fail(response, Response.fail(ResponseCodeEnum.USER_NOT_ENABLED.getErrorCode(), exception.getMessage()));
                return;
            }
        } else if (exception instanceof UsernameNotFoundException) {
            // 用户名不存在或账户未启用，返回原始异常信息
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
            return;
        } else if (exception instanceof BadCredentialsException) {
            // 用户名或密码错误
            ResultUtil.fail(response, Response.fail(ResponseCodeEnum.USERNAME_OR_PWD_ERROR));
            return;
        }

        // 登录失败
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.LOGIN_FAIL));
    }
}
