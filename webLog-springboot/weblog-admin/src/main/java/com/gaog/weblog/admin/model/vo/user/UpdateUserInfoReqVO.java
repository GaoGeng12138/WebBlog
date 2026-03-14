package com.gaog.weblog.admin.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author Gao
 * @description 更新用户信息请求 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新用户信息请求 VO")
public class UpdateUserInfoReqVO {

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long id;

    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "用户名")
    @Length(max = 100, message = "用户名长度不能超过100个字符")
    private String username;

    @Length(max = 100, message = "昵称长度不能超过100个字符")
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "密码")
    @Length(max = 100, message = "密码长度不能超过100个字符")
    private String password;

    @Length(max = 255, message = "个性签名长度不能超过255个字符")
    @ApiModelProperty(value = "个性签名")
    private String introduction;

    @Length(max = 255, message = "头像URL长度不能超过255个字符")
    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @Length(max = 200, message = "GitHub链接长度不能超过200个字符")
    @ApiModelProperty(value = "GitHub链接")
    private String githubUrl;

    @Length(max = 200, message = "Twitter链接长度不能超过200个字符")
    @ApiModelProperty(value = "Twitter链接")
    private String twitterUrl;

    @Length(max = 200, message = "微博链接长度不能超过200个字符")
    @ApiModelProperty(value = "微博链接")
    private String weiboUrl;
}
