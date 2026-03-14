package com.gaog.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Assign role to user request VO
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Assign Role to User Request")
public class AssignRoleReqVO {

    @ApiModelProperty(value = "User ID", required = true)
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @ApiModelProperty(value = "Role IDs list", required = true)
    @NotNull(message = "Role IDs cannot be null")
    private List<Long> roleIds;
}
