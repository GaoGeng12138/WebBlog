package com.gaog.weblog.web.model.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端基本设置响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("前端基本设置")
public class FindBasicSettingRspVO {

    @ApiModelProperty("网站标题")
    private String title;

    @ApiModelProperty("网站标语")
    private String slogan;

    @ApiModelProperty("网站描述")
    private String description;

    @ApiModelProperty("网站图标URL")
    private String logoUrl;

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
}
