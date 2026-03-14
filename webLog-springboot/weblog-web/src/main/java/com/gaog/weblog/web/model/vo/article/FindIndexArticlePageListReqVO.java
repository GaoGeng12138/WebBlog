package com.gaog.weblog.web.model.vo.article;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 *
 * @Author: gaoge
 * @Date: 2025/12/7 23:14
 * @Version: 1.0
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "首页查询文章分页 VO")
public class FindIndexArticlePageListReqVO extends BasePageQuery {
    /**
     * 关键字
     */
    private String name;

    /**
     * 用户ID（可选，如果传入则查询该用户发布的文章，不传则查询全部文章）
     */
    private Long userId;
}