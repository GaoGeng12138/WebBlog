package com.gaog.weblog.admin.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSJ
 * @date 2025/12/2 10:30
 * @description Dashboard statistics response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsRspVO {

    /**
     * 文章总数
     */
    private Long articleTotal;

    /**
     * 分类总数
     */
    private Long categoryTotal;

    /**
     * 标签总数
     */
    private Long tagTotal;

    /**
     * 总浏览量
     */
    private Long pvTotal;
}