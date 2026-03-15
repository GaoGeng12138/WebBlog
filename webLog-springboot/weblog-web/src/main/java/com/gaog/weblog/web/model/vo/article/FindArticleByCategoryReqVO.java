package com.gaog.weblog.web.model.vo.article;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaog
 * @description 根据分类查询文章请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "根据分类查询文章请求VO")
public class FindArticleByCategoryReqVO extends BasePageQuery {
    /**
     * 关键字
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;
}
