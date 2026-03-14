package com.gaog.weblog.web.model.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端权限配置响应VO
 * 用于前端判断是否展示相应功能
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("前端权限配置")
public class FindPermissionSettingRspVO {

    @ApiModelProperty("是否启用评论")
    private Boolean commentEnabled;

    @ApiModelProperty("是否启用点赞")
    private Boolean likeEnabled;

    @ApiModelProperty("是否启用收藏")
    private Boolean favoriteEnabled;

    @ApiModelProperty("是否允许用户注册")
    private Boolean userRegisterEnabled;

    @ApiModelProperty("是否允许用户发布文章")
    private Boolean userPublishEnabled;

    @ApiModelProperty("文章是否需要审核")
    private Boolean articleReviewRequired;

    @ApiModelProperty("评论是否需要审核")
    private Boolean commentReviewRequired;

    @ApiModelProperty("是否允许匿名评论")
    private Boolean anonymousCommentEnabled;
}
