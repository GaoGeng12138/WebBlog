package com.gaog.weblog.admin.model.vo.article;

import com.gaog.weblog.common.model.vo.SelectRspVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.gaog.weblog.common.model.vo.SelectRspVO;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ZSJ
 * @date 2025/11/25 16:56
 * @description 查询文章详情出参 VO
 */
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
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 标签 ID 集合
     */
    private List<SelectRspVO> tags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
