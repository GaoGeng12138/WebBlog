package com.gaog.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Add role request VO
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Add Role Request")
public class AddRoleReqVO {

    @ApiModelProperty(value = "Role name (e.g., ROLE_ADMIN)", required = true)
    @NotBlank(message = "Role name cannot be empty")
    private String name;

    @ApiModelProperty(value = "Role description")
    private String description;

    @ApiModelProperty(value = "Is enabled: true/false", required = true)
    private Boolean isEnabled;
}
