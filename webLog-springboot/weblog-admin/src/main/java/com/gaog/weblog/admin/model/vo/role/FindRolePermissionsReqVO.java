package com.gaog.weblog.admin.model.vo.role;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FindRolePermissionsReqVO {

    @NotNull(message = "角色 ID 不能为空")
    private Long roleId;
}
