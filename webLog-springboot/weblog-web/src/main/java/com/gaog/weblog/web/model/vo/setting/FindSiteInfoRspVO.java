package com.gaog.weblog.web.model.vo.setting;

import com.gaog.weblog.common.annotation.SensitiveField;
import com.gaog.weblog.common.domain.dos.BlogSettingDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台站点信息响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("前台站点信息")
public class FindSiteInfoRspVO {

    @ApiModelProperty("网站标题")
    @SensitiveField(request = false)
    private String title;

    @ApiModelProperty("网站描述")
    @SensitiveField(request = false)
    private String description;

    @ApiModelProperty("网站图标URL")
    @SensitiveField(request = false)
    private String logoUrl;

    @ApiModelProperty("前台文章列表每页数量")
    private Integer frontendArticlePageSize;

    @ApiModelProperty("活跃度等级规则JSON")
    @SensitiveField(request = false)
    private String activityLevelRules;

    @ApiModelProperty("活跃度加分规则JSON")
    @SensitiveField(request = false)
    private String activityScoreRules;

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

    public static FindSiteInfoRspVO fromDO(BlogSettingDO settingDO) {
        if (settingDO == null) {
            return FindSiteInfoRspVO.builder().build();
        }

        return FindSiteInfoRspVO.builder()
                .title(settingDO.getTitle())
                .description(settingDO.getDescription())
                .logoUrl(settingDO.getLogoUrl())
                .frontendArticlePageSize(settingDO.getFrontendArticlePageSize())
                .activityLevelRules(settingDO.getActivityLevelRules())
                .activityScoreRules(settingDO.getActivityScoreRules())
                .githubEnabled(settingDO.getGithubEnabled())
                .githubShowFront(settingDO.getGithubShowFront())
                .githubShowRegister(settingDO.getGithubShowRegister())
                .twitterEnabled(settingDO.getTwitterEnabled())
                .twitterShowFront(settingDO.getTwitterShowFront())
                .twitterShowRegister(settingDO.getTwitterShowRegister())
                .weiboEnabled(settingDO.getWeiboEnabled())
                .weiboShowFront(settingDO.getWeiboShowFront())
                .weiboShowRegister(settingDO.getWeiboShowRegister())
                .build();
    }
}
