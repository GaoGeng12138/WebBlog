package com.gaog.weblog.admin.model.vo.user;

import com.gaog.weblog.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gao
 * @description 查询用户列表请求 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询用户列表请求 VO")
public class FindUserListReqVO extends BasePageQuery {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "启用状态：null-全部，true-已启用，false-未启用")
    private Boolean isEnabled;
}
