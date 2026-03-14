package com.gaog.weblog.admin.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Gao
 * @description 更新用户状态请求 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新用户状态请求 VO")
public class UpdateUserStatusReqVO {

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long id;

    @NotNull(message = "启用状态不能为空")
    @ApiModelProperty(value = "启用状态：true-启用，false-禁用", required = true)
    private Boolean isEnabled;
}
