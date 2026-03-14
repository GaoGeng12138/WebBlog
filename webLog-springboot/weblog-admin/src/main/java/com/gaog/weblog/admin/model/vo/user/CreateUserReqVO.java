package com.gaog.weblog.admin.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Gao
 * @description 创建用户请求 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "创建用户请求 VO")
public class CreateUserReqVO {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @Length(max = 100, message = "昵称长度不能超过100个字符")
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @Length(max = 255, message = "个性签名长度不能超过255个字符")
    @ApiModelProperty(value = "个性签名")
    private String introduction;

    @Length(max = 255, message = "头像URL长度不能超过255个字符")
    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @NotNull(message = "启用状态不能为空")
    @ApiModelProperty(value = "是否启用：true-启用，false-禁用", required = true)
    private Boolean isEnabled;

    @ApiModelProperty(value = "角色ID列表")
    private List<Long> roleIds;
}
