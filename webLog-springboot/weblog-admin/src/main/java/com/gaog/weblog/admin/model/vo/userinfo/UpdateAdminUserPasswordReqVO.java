package com.gaog.weblog.admin.model.vo.userinfo;

import com.gaog.weblog.common.annotation.SensitiveField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/21 23:12
 * @Version: 1.0
 * @Description:修改用户密码 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "修改用户密码 VO")
public class UpdateAdminUserPasswordReqVO implements Serializable {

    private static final long serialVersionUID = 3581428302630039848L;
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名")
    @SensitiveField(response = false)
    private String username;

    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码")
    @SensitiveField(response = false)
    private String oldPassword;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码")
    @SensitiveField(response = false)
    private String password;

    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty(value = "确认密码")
    @SensitiveField(response = false)
    private String confirmPassword;
}
