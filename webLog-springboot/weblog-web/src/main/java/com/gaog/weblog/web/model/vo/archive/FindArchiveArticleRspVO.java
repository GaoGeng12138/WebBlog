package com.gaog.weblog.web.model.vo.archive;

import com.gaog.weblog.web.model.vo.article.FindCategoryListRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/9 23:43
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArchiveArticleRspVO {
    private Long id;
    private String cover;
    private String title;
    /**
     * 分类信息
     */
    private FindCategoryListRspVO category;

    /**
     * 浏览次数
     */
    private Integer viewCount;
    /**
     * 发布日期
     */
    private LocalDate createDate;

    /**
     * 发布的月份（此字段不需要展示在前端，主要用于按月份分组使用）
     */
    private YearMonth createMonth;
}
