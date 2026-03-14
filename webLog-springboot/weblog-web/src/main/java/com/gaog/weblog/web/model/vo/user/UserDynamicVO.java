package com.gaog.weblog.web.model.vo.user;

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
@ApiModel("User Dynamic VO")
public class UserDynamicVO {

    @ApiModelProperty("Dynamic ID")
    private Long id;

    @ApiModelProperty("Dynamic type: 1-article, 2-comment, 3-favorite")
    private Integer type;

    @ApiModelProperty("Related item ID (articleId, commentId, etc.)")
    private Long relatedId;

    @ApiModelProperty("Title or brief description")
    private String title;

    @ApiModelProperty("Content preview")
    private String contentPreview;

    @ApiModelProperty("Create time")
    private LocalDateTime createTime;
}