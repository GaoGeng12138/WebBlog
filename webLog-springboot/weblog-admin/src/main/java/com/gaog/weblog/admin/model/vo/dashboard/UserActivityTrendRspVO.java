package com.gaog.weblog.admin.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qwen
 * @date 2025/12/20
 * @description User activity trend response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityTrendRspVO {

    /**
     * 日期 (yyyy-MM-dd)
     */
    private String date;

    /**
     * 活跃用户数
     */
    private Long activeUserCount;

}