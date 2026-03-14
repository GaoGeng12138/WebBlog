package com.gaog.weblog.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 14:09
 * @Version: 1.0
 * @Description:
 */
public class JsonUtil {

    private static final ObjectMapper INSTANCE = new ObjectMapper();

    /**
     * 将对象转换为JSON字符串
     *
     * @param object 需要转换为JSON字符串的对象
     * @return 转换后的JSON字符串，如果转换失败则返回对象的toString()结果
     */
    public static String toJsonString(Object object) {
        try {
            // 使用Jackson实例将对象转换为JSON字符串
            return INSTANCE.writeValueAsString(object);
        } catch (Exception e) {
            // 如果转换过程中发生异常，则返回对象的toString()方法结果
            return object.toString();
        }
    }
}
