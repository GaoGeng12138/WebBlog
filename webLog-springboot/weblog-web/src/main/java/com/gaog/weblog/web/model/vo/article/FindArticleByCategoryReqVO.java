package com.gaog.weblog.web.model.vo.article;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author gaog
 * @description 根据分类查询文章请求VO
 */
@Data
@Builder
@ApiModel(value = "根据分类查询文章请求VO")
public class FindArticleByCategoryReqVO extends BasePageQuery {
    /**
     * 分类ID
     */
    private Long categoryId;
}