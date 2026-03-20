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

/**
 * @author Gao
 * @description 前台更新个人资料请求 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "前台更新个人资料请求 VO")
public class UpdateUserProfileReqVO {

    @Length(max = 100, message = "昵称长度不能超过100个字符")
    @ApiModelProperty(value = "昵称")
    @SensitiveField(response = false)
    private String nickname;

    @Length(max = 255, message = "个性签名长度不能超过255个字符")
    @ApiModelProperty(value = "个性签名")
    private String introduction;

    @Length(max = 255, message = "头像URL长度不能超过255个字符")
    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱")
    @SensitiveField(response = false)
    private String email;

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
