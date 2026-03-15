package com.gaog.weblog.web.controller;

import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import com.gaog.weblog.common.domain.mapper.SiteSettingMapper;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.web.model.vo.setting.FindBasicSettingRspVO;
import com.gaog.weblog.web.model.vo.setting.FindPermissionSettingRspVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "站点信息模块")
@RestController
public class SiteController {

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @GetMapping("/site/info")
    @ApiOperation(value = "获取站点信息")
    @ApiOperationLog(description = "获取站点信息")
    public Response<BlogSettingDO> getSiteInfo() {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        return Response.success(setting);
    }

    @PostMapping("/site/permissions")
    @ApiOperation(value = "获取站点权限配置")
    @ApiOperationLog(description = "获取站点权限配置")
    public Response<FindPermissionSettingRspVO> getPermissionSettings() {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        
        // 如果没有配置，返回默认值
        if (setting == null) {
            FindPermissionSettingRspVO defaultSettings = FindPermissionSettingRspVO.builder()
                    .commentEnabled(true)
                    .likeEnabled(true)
                    .favoriteEnabled(true)
                    .userRegisterEnabled(false)
                    .userPublishEnabled(false)
                    .articleReviewRequired(false)
                    .commentReviewRequired(false)
                    .anonymousCommentEnabled(false)
                    .build();
            return Response.success(defaultSettings);
        }
        
        // 转换为权限配置 VO
        FindPermissionSettingRspVO permissionSettings = FindPermissionSettingRspVO.builder()
                .commentEnabled(setting.getCommentEnabled() != null ? setting.getCommentEnabled() : true)
                .likeEnabled(setting.getLikeEnabled() != null ? setting.getLikeEnabled() : true)
                .favoriteEnabled(setting.getFavoriteEnabled() != null ? setting.getFavoriteEnabled() : true)
                .userRegisterEnabled(setting.getUserRegisterEnabled() != null ? setting.getUserRegisterEnabled() : false)
                .userPublishEnabled(setting.getUserPublishEnabled() != null ? setting.getUserPublishEnabled() : false)
                .articleReviewRequired(setting.getArticleReviewRequired() != null ? setting.getArticleReviewRequired() : false)
                .commentReviewRequired(setting.getCommentReviewRequired() != null ? setting.getCommentReviewRequired() : false)
                .anonymousCommentEnabled(setting.getAnonymousCommentEnabled() != null ? setting.getAnonymousCommentEnabled() : false)
                .build();
        
        return Response.success(permissionSettings);
    }

    @PostMapping("/site/basic")
    @ApiOperation(value = "获取站点基本设置")
    @ApiOperationLog(description = "获取站点基本设置")
    public Response<FindBasicSettingRspVO> getBasicSettings() {
        BlogSettingDO setting = siteSettingMapper.findSingleton();
        
        // 如果没有配置，返回空值
        if (setting == null) {
            FindBasicSettingRspVO defaultSettings = FindBasicSettingRspVO.builder().build();
            return Response.success(defaultSettings);
        }
        
        // 转换为基本设置 VO
        FindBasicSettingRspVO basicSettings = FindBasicSettingRspVO.builder()
                .title(setting.getTitle())
                .description(setting.getDescription())
                .logoUrl(setting.getLogoUrl())
                .githubEnabled(setting.getGithubEnabled())
                .githubShowFront(setting.getGithubShowFront())
                .githubShowRegister(setting.getGithubShowRegister())
                .twitterEnabled(setting.getTwitterEnabled())
                .twitterShowFront(setting.getTwitterShowFront())
                .twitterShowRegister(setting.getTwitterShowRegister())
                .weiboEnabled(setting.getWeiboEnabled())
                .weiboShowFront(setting.getWeiboShowFront())
                .weiboShowRegister(setting.getWeiboShowRegister())
                .build();
        
        return Response.success(basicSettings);
    }
}
