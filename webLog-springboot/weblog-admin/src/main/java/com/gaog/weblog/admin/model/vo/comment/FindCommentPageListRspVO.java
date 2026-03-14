package com.gaog.weblog.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Find comment page list response VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Find Comment Page List Response")
public class FindCommentPageListRspVO {

    @ApiModelProperty("Comment ID")
    private Long id;

    @ApiModelProperty("Article ID")
    private Long articleId;

    @ApiModelProperty("Article title")
    private String articleTitle;

    @ApiModelProperty("User ID")
    private Long userId;

    @ApiModelProperty("Commenter nickname")
    private String nickname;

    @ApiModelProperty("Commenter email")
    private String email;

    @ApiModelProperty("Comment content")
    private String content;

    @ApiModelProperty("IP address")
    private String ipAddress;

    @ApiModelProperty("Status (0-pending, 1-approved, 2-rejected)")
    private Integer status;

    @ApiModelProperty("Like count")
    private Long likeCount;

    @ApiModelProperty("Is top comment")
    private Boolean isTop;

    @ApiModelProperty("Create time")
    private LocalDateTime createTime;

    @ApiModelProperty("Parent comment ID")
    private Long parentId;
}
