package com.gaog.weblog.web.model.vo.user;

import com.gaog.weblog.common.annotation.SensitiveField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author Gao
 * @description 用户注册请求 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户注册请求 VO")
public class RegisterUserReqVO {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @ApiModelProperty(value = "用户名", required = true)
    @SensitiveField(response = false)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @ApiModelProperty(value = "密码", required = true)
    @SensitiveField(response = false)
    private String password;

    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱")
    @SensitiveField(response = false)
    private String email;

    @Length(max = 100, message = "昵称长度不能超过100个字符")
    @ApiModelProperty(value = "昵称")
    @SensitiveField(response = false)
    private String nickname;

    @Length(max = 200, message = "个性签名长度不能超过200个字符")
    @ApiModelProperty(value = "个性签名")
    private String introduction;

    @Length(max = 100, message = "头像长度不能超过100个字符")
    @ApiModelProperty(value = "头像")
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
