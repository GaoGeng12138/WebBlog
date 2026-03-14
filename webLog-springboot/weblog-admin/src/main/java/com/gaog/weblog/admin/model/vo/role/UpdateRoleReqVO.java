package com.gaog.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Update role request VO
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Update Role Request")
public class UpdateRoleReqVO {

    @ApiModelProperty(value = "Role ID", required = true)
    @NotNull(message = "Role ID cannot be null")
    private Long id;

    @ApiModelProperty(value = "Role description")
    private String description;

    @ApiModelProperty(value = "Is enabled: true/false")
    private Boolean isEnabled;
}
