package com.gaog.weblog.admin.model.vo.comment;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Find comment page list request VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Find Comment Page List Request")
public class FindCommentPageListReqVO extends BasePageQuery {

    @ApiModelProperty("Article ID")
    private Long articleId;

    @ApiModelProperty("Comment status (0-pending, 1-approved, 2-rejected)")
    private Integer status;

    @ApiModelProperty("Start date")
    private LocalDate startDate;

    @ApiModelProperty("End date")
    private LocalDate endDate;
}
