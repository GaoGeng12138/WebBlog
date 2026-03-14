package com.gaog.weblog.common.exception;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 15:26
 * @Version: 1.0
 * @Description:
 */
public interface BaseExceptionInterface {
    /**
     * 获取错误码的方法
     *
     * @return 返回一个String类型的错误码
     */
    String getErrorCode();

/**
 * 获取错误信息的方法
 * 该方法用于返回一个错误信息字符串
 *
 * @return 返回一个包含错误信息的字符串
 */
    String getErrorMessage();
}
