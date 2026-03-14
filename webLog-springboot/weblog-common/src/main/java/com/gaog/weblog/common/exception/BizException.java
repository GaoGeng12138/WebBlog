package com.gaog.weblog.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 15:27
 * @Version: 1.0
 * @Description: 自定义业务异常
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface) {
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }
}
