package com.gaog.weblog.admin.controller;

import com.gaog.weblog.admin.model.vo.user.CreateUserReqVO;
import com.gaog.weblog.admin.model.vo.user.DeleteUserReqVO;
import com.gaog.weblog.admin.model.vo.user.FindUserListReqVO;
import com.gaog.weblog.admin.model.vo.user.UpdateUserInfoReqVO;
import com.gaog.weblog.admin.model.vo.user.UpdateUserStatusReqVO;
import com.gaog.weblog.admin.model.vo.userinfo.UpdateAdminUserPasswordReqVO;
import com.gaog.weblog.admin.service.AdminUserService;
import com.gaog.weblog.common.aspect.ApiOperationLog;
import com.gaog.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/21 23:18
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 用户模块")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    @PostMapping("/password/update")
    @ApiOperation(value = "修改用户密码")
    @ApiOperationLog(description = "修改用户密码")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:password','admin:user:update')")
    public Response updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return userService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/user/info")
    @ApiOperation(value = "获取用户信息")
    @ApiOperationLog(description = "获取用户信息")
    public Response findUserInfo() {
        return userService.findUserInfo();
    }

    @PostMapping("/user/list")
    @ApiOperation(value = "查询用户列表")
    @ApiOperationLog(description = "查询用户列表")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:list')")
    public Response findUserList(@RequestBody @Validated FindUserListReqVO findUserListReqVO) {
        return userService.findUserList(findUserListReqVO);
    }

    @PostMapping("/user/status/update")
    @ApiOperation(value = "更新用户状态")
    @ApiOperationLog(description = "更新用户状态")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:status')")
    public Response updateUserStatus(@RequestBody @Validated UpdateUserStatusReqVO updateUserStatusReqVO) {
        return userService.updateUserStatus(updateUserStatusReqVO);
    }

    @PostMapping({"/user/create", "/user/add"})
    @ApiOperation(value = "创建用户")
    @ApiOperationLog(description = "创建用户")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:create')")
    public Response createUser(@RequestBody @Validated CreateUserReqVO createUserReqVO) {
        return userService.createUser(createUserReqVO);
    }

    @PostMapping("/user/delete")
    @ApiOperation(value = "删除用户")
    @ApiOperationLog(description = "删除用户")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:delete')")
    public Response deleteUser(@RequestBody @Validated DeleteUserReqVO deleteUserReqVO) {
        return userService.deleteUser(deleteUserReqVO);
    }

    @DeleteMapping("/user/delete/{id}")
    @ApiOperation(value = "删除用户")
    @ApiOperationLog(description = "删除用户")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:delete')")
    public Response deleteUser(@PathVariable Long id) {
        return userService.deleteUser(DeleteUserReqVO.builder().id(id).build());
    }

    @PostMapping("/user/info/update")
    @ApiOperation(value = "更新用户信息")
    @ApiOperationLog(description = "更新用户信息")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:update')")
    public Response updateUserInfo(@RequestBody @Validated UpdateUserInfoReqVO updateUserInfoReqVO) {
        return userService.updateUserInfo(updateUserInfoReqVO);
    }

    @PostMapping("/user/roles")
    @ApiOperation(value = "获取用户角色")
    @ApiOperationLog(description = "获取用户角色")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:role-assign','admin:user:list')")
    public Response getUserRoles(@RequestParam Long userId) {
        return userService.getUserRoles(userId);
    }

    @PostMapping("/user/select/list")
    @ApiOperation(value = "获取用户下拉列表")
    @ApiOperationLog(description = "获取用户下拉列表")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','admin:user:list','admin:user:role-assign')")
    public Response findUserSelectList() {
        return userService.findUserSelectList();
    }

}
