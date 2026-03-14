package com.gaog.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Find role page list response VO
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Find Role Page List Response")
public class FindRolePageListRspVO {

    @ApiModelProperty(value = "Role ID")
    private Long id;

    @ApiModelProperty(value = "Role name")
    private String name;

    @ApiModelProperty(value = "Role description")
    private String description;

    @ApiModelProperty(value = "Is enabled")
    private Boolean isEnabled;

    @ApiModelProperty(value = "Create time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "Update time")
    private LocalDateTime updateTime;
}
