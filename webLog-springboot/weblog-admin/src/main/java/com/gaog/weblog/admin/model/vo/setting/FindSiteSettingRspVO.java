package com.gaog.weblog.admin.model.vo.setting;

import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("站点设置出参")
public class FindSiteSettingRspVO {

    @ApiModelProperty("网站标题")
    private String title;

    @ApiModelProperty("网站描述")
    private String description;

    @ApiModelProperty("网站图标URL")
    private String logoUrl;

    @ApiModelProperty("前台文章列表每页数量")
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

    public static FindSiteSettingRspVO fromDO(BlogSettingDO settingDO) {
        FindSiteSettingRspVO vo = new FindSiteSettingRspVO();
        if (settingDO == null) {
            return vo;
        }
        vo.setTitle(settingDO.getTitle());
        vo.setDescription(settingDO.getDescription());
        vo.setLogoUrl(settingDO.getLogoUrl());
        vo.setFrontendArticlePageSize(settingDO.getFrontendArticlePageSize());
        vo.setGithubEnabled(settingDO.getGithubEnabled());
        vo.setGithubShowFront(settingDO.getGithubShowFront());
        vo.setGithubShowRegister(settingDO.getGithubShowRegister());
        vo.setTwitterEnabled(settingDO.getTwitterEnabled());
        vo.setTwitterShowFront(settingDO.getTwitterShowFront());
        vo.setTwitterShowRegister(settingDO.getTwitterShowRegister());
        vo.setWeiboEnabled(settingDO.getWeiboEnabled());
        vo.setWeiboShowFront(settingDO.getWeiboShowFront());
        vo.setWeiboShowRegister(settingDO.getWeiboShowRegister());
        vo.setCommentEnabled(settingDO.getCommentEnabled());
        vo.setLikeEnabled(settingDO.getLikeEnabled());
        vo.setFavoriteEnabled(settingDO.getFavoriteEnabled());
        vo.setUserRegisterEnabled(settingDO.getUserRegisterEnabled());
        vo.setUserPublishEnabled(settingDO.getUserPublishEnabled());
        vo.setArticleReviewRequired(settingDO.getArticleReviewRequired());
        vo.setCommentReviewRequired(settingDO.getCommentReviewRequired());
        vo.setAnonymousCommentEnabled(settingDO.getAnonymousCommentEnabled());
        return vo;
    }
}
