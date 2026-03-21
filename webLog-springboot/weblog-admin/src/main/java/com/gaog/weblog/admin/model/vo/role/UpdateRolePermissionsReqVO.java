package com.gaog.weblog.admin.model.vo.role;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateRolePermissionsReqVO {

    @NotNull(message = "角色 ID 不能为空")
    private Long roleId;

    private List<Long> permissionIds;
}
