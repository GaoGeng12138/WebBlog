package com.gaog.weblog.admin.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ZSJ
 * @date 2025/12/2 10:45
 * @description Latest article response VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LatestArticleRspVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 浏览量
     */
    private Long readNum;
}