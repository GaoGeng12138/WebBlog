package com.gaog.weblog.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Delete comment request VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Delete Comment Request")
public class DeleteCommentReqVO {

    @ApiModelProperty(value = "Comment ID", required = true)
    @NotNull(message = "Comment ID cannot be null")
    private Long commentId;
}
