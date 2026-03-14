package com.gaog.weblog.web.model.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Add comment request VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Add Comment Request")
public class AddCommentReqVO {

    @ApiModelProperty(value = "Article ID", required = true)
    @NotNull(message = "Article ID cannot be null")
    private Long articleId;

    @ApiModelProperty(value = "Parent comment ID (null for top-level comment)")
    private Long parentId;

    @ApiModelProperty(value = "Reply to comment ID")
    private Long replyToId;

    @ApiModelProperty(value = "Comment content", required = true)
    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    @ApiModelProperty(value = "Nickname (for anonymous comment)")
    private String nickname;

    @ApiModelProperty(value = "Email (for anonymous comment)")
    private String email;

    @ApiModelProperty(value = "Website (optional)")
    private String website;
}
