package com.gaog.weblog.web.model.vo.user;

import com.gaog.weblog.common.annotation.SensitiveField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gao
 * @description 用户注册响应 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户注册响应 VO")
public class RegisterUserRspVO {

    @ApiModelProperty(value = "用户名")
    @SensitiveField(request = false)
    private String username;

    @ApiModelProperty(value = "提示信息")
    private String message;
}
