package com.gaog.weblog.admin.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZSJ
 * @date 2025/12/2 10:35
 * @description Article publish trend response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticlePublishTrendRspVO {


    /**
     * 日期 (yyyy-MM-dd)
     */
    private String date;

    /**
     * 发布数量
     */
    private Long count;

}