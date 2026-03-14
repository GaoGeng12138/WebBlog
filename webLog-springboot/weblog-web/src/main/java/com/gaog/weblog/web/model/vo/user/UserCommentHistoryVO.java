package com.gaog.weblog.web.model.vo.user;

import com.gaog.weblog.web.model.vo.comment.FindCommentDetailRspVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("User Comment History VO")
public class UserCommentHistoryVO {

    @ApiModelProperty("Comment ID")
    private Long id;

    @ApiModelProperty("Article ID")
    private Long articleId;

    @ApiModelProperty("Article Title")
    private String articleTitle;

    @ApiModelProperty("Comment content")
    private String content;

    @ApiModelProperty("Like count")
    private Long likeCount;

    @ApiModelProperty("Create time")
    private LocalDateTime createTime;
}