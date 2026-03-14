package com.gaog.weblog.web.model.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment detail response VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Comment Detail")
public class FindCommentDetailRspVO {

    @ApiModelProperty("Comment ID")
    private Long id;

    @ApiModelProperty("Article ID")
    private Long articleId;

    @ApiModelProperty("User ID")
    private Long userId;

    @ApiModelProperty("Parent comment ID")
    private Long parentId;

    @ApiModelProperty("Reply to comment ID")
    private Long replyToId;

    @ApiModelProperty("Reply to user nickname")
    private String replyToNickname;

    @ApiModelProperty("Commenter nickname")
    private String nickname;

    @ApiModelProperty("Commenter avatar")
    private String avatar;

    @ApiModelProperty("Comment content")
    private String content;

    @ApiModelProperty("Website")
    private String website;

    @ApiModelProperty("Like count")
    private Long likeCount;

    @ApiModelProperty("Is top comment")
    private Boolean isTop;

    @ApiModelProperty("Create time")
    private LocalDateTime createTime;

    @ApiModelProperty("Reply list")
    private List<FindCommentDetailRspVO> replies;

    @ApiModelProperty("Is author (article author)")
    private Boolean isAuthor;
}
