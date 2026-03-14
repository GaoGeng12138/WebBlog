package com.gaog.weblog.admin.model.vo.role;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Find role page list request VO
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Find Role Page List Request")
public class FindRolePageListReqVO extends BasePageQuery {

    @ApiModelProperty(value = "Role name (supports fuzzy search)")
    private String name;

    @ApiModelProperty(value = "Role status: true = enabled, false = disabled")
    private Boolean isEnabled;
}
