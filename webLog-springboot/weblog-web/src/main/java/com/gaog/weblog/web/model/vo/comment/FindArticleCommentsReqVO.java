package com.gaog.weblog.web.model.vo.comment;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Get article comments request VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Get Article Comments Request")
public class FindArticleCommentsReqVO extends BasePageQuery {

    @ApiModelProperty(value = "Article ID", required = true)
    @NotNull(message = "Article ID cannot be null")
    private Long articleId;
}
