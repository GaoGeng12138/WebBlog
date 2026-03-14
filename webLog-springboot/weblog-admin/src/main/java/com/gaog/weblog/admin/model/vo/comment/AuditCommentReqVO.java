package com.gaog.weblog.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Audit comment request VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Audit Comment Request")
public class AuditCommentReqVO {

    @ApiModelProperty(value = "Comment ID", required = true)
    @NotNull(message = "Comment ID cannot be null")
    private Long commentId;

    @ApiModelProperty(value = "Status (1-approved, 2-rejected)", required = true)
    @NotNull(message = "Status cannot be null")
    private Integer status;

    @ApiModelProperty("Rejection reason")
    private String reason;
}
