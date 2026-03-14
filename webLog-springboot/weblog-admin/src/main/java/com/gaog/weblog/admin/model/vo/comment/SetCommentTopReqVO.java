package com.gaog.weblog.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Set comment top request VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Set Comment Top Request")
public class SetCommentTopReqVO {

    @ApiModelProperty(value = "Comment ID", required = true)
    @NotNull(message = "Comment ID cannot be null")
    private Long commentId;

    @ApiModelProperty(value = "Is top", required = true)
    @NotNull(message = "Is top cannot be null")
    private Boolean isTop;
}
