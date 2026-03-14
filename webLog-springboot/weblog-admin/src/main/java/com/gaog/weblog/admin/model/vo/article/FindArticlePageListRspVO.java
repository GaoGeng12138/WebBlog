package com.gaog.weblog.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ZSJ
 * @date 2025/11/25 16:55
 * @description 查询文章分页数据出参 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询文章分页数据出参 VO")
public class FindArticlePageListRspVO {
    /**
     * 文章 ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章封面
     */
    private String cover;

    /**
     * 作者
     */
    private String author;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章分类
     */
    private String category;

    /**
     * 文章标签
     */
    private List<String> tags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
