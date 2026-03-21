package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.role.AddRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.AssignRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.DeleteRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRolePageListReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRolePermissionsReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRoleSelectListReqVO;
import com.gaog.weblog.admin.model.vo.role.UpdateRolePermissionsReqVO;
import com.gaog.weblog.admin.model.vo.role.UpdateRoleReqVO;
import com.gaog.weblog.admin.service.AdminRoleService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Role management controller
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 * @Description: Admin role management API controller
 */
@RestController
@RequestMapping("/admin/role")
@Api(tags = "Admin 角色管理")
public class AdminRoleController {

    @Autowired
    private AdminRoleService roleService;

    @PostMapping("/add")
    @ApiOperation(value = "添加角色")
    @ApiOperationLog(description = "添加角色")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:add')")
    public Response addRole(@RequestBody @Validated AddRoleReqVO addRoleReqVO) {
        return roleService.addRole(addRoleReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询角色分页列表")
    @ApiOperationLog(description = "查询角色分页列表")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:list')")
    public Response findRolePageList(@RequestBody @Validated FindRolePageListReqVO findRolePageListReqVO) {
        return roleService.findRolePageList(findRolePageListReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新角色")
    @ApiOperationLog(description = "更新角色")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:update')")
    public Response updateRole(@RequestBody @Validated UpdateRoleReqVO updateRoleReqVO) {
        return roleService.updateRole(updateRoleReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除角色")
    @ApiOperationLog(description = "删除角色")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:delete')")
    public Response deleteRole(@RequestBody @Validated DeleteRoleReqVO deleteRoleReqVO) {
        return roleService.deleteRole(deleteRoleReqVO);
    }

    @PostMapping("/select/list")
    @ApiOperation(value = "查询角色选择列表")
    @ApiOperationLog(description = "查询角色选择列表")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:role-assign','admin:role:list')")
    public Response findRoleSelectList(@RequestBody @Validated FindRoleSelectListReqVO findRoleSelectListReqVO) {
        return roleService.findRoleSelectList(findRoleSelectListReqVO);
    }

    @PostMapping("/assign")
    @ApiOperation(value = "为用户分配角色")
    @ApiOperationLog(description = "为用户分配角色")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:role-assign')")
    public Response assignRole(@RequestBody @Validated AssignRoleReqVO assignRoleReqVO) {
        return roleService.assignRole(assignRoleReqVO);
    }

    @PostMapping("/permissions")
    @ApiOperation(value = "查询角色权限")
    @ApiOperationLog(description = "查询角色权限")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:permission-assign')")
    public Response findRolePermissions(@RequestBody @Validated FindRolePermissionsReqVO reqVO) {
        return roleService.findRolePermissions(reqVO);
    }

    @PostMapping("/permissions/update")
    @ApiOperation(value = "更新角色权限")
    @ApiOperationLog(description = "更新角色权限")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:permission-assign')")
    public Response updateRolePermissions(@RequestBody @Validated UpdateRolePermissionsReqVO reqVO) {
        return roleService.updateRolePermissions(reqVO);
    }
}
