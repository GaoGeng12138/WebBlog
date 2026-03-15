package com.gaog.weblog.admin.model.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@ApiModel("更新站点设置入参")
public class UpdateSiteSettingReqVO {

    @ApiModelProperty("站点标题")
    @Size(max = 50, message = "网站标题最多50字")
    private String title;

    @ApiModelProperty("站点描述")
    @Size(max = 200, message = "网站描述最多200字")
    private String description;

    @ApiModelProperty("网站图标URL")
    private String logoUrl;

    @ApiModelProperty("前台文章列表每页数量")
    @Min(value = 4, message = "前台文章列表每页数量不能小于4")
    @Max(value = 60, message = "前台文章列表每页数量不能大于60")
    private Integer frontendArticlePageSize;

    @ApiModelProperty("是否启用GitHub功能")
    private Boolean githubEnabled;

    @ApiModelProperty("GitHub前台展示")
    private Boolean githubShowFront;

    @ApiModelProperty("GitHub注册页展示")
    private Boolean githubShowRegister;

    @ApiModelProperty("是否启用Twitter功能")
    private Boolean twitterEnabled;

    @ApiModelProperty("Twitter前台展示")
    private Boolean twitterShowFront;

    @ApiModelProperty("Twitter注册页展示")
    private Boolean twitterShowRegister;

    @ApiModelProperty("是否启用微博功能")
    private Boolean weiboEnabled;

    @ApiModelProperty("微博前台展示")
    private Boolean weiboShowFront;

    @ApiModelProperty("微博注册页展示")
    private Boolean weiboShowRegister;

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
