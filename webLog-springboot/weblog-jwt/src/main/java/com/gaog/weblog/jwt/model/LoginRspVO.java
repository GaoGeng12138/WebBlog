package com.gaog.weblog.jwt.model;

import com.gaog.weblog.common.annotation.SensitiveField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/16 22:48
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRspVO {
    /**
     * Token
     */
    @SensitiveField(request = false)
    private String token;
}
