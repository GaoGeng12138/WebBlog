package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.permission.AdminPermissionCatalog;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/permissions")
@Api(tags = "Admin 权限模块")
public class AdminPermissionController {

    @PostMapping("/all")
    @ApiOperation(value = "获取所有权限定义")
    @ApiOperationLog(description = "获取所有权限定义")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:role:permission-assign')")
    public Response findAllPermissions() {
        return Response.success(AdminPermissionCatalog.getPermissionTree());
    }
}
