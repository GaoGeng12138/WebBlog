package com.gaog.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qwen
 * @date 2025/12/20
 * @description User activity trend response VO for web module
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityTrendRspVO {

    /**
     * 日期
     */
    private String date;

    /**
     * 活跃度分数
     */
    private Integer score;

}