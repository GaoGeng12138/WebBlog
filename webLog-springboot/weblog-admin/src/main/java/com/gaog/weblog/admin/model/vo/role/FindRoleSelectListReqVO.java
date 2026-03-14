package com.gaog.weblog.admin.model.vo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Find role select list request VO
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@ApiModel(description = "Find Role Select List Request")
public class FindRoleSelectListReqVO {
    // Empty VO for query all enabled roles
}
