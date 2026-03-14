package com.gaog.weblog.web.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询文章详情出参 VO")
public class FindArticleDetailRspVO {

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
     * 文章正文（HTML）
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 分类信息
     */
    private FindCategoryListRspVO category;

    /**
     * 标签集合
     */
    private List<FindTagListRspVO> tags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 阅读量
     */
    private Long readNum;

    /**
     * 上一篇文章
     */
    private FindPreNextArticleRspVO preArticle;
    /**
     * 下一篇文章
     */
    private FindPreNextArticleRspVO nextArticle;
}