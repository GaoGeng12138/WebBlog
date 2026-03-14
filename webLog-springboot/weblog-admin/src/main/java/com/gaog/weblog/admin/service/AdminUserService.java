package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.user.CreateUserReqVO;
import com.gaog.weblog.admin.model.vo.user.DeleteUserReqVO;
import com.gaog.weblog.admin.model.vo.user.FindUserListReqVO;
import com.gaog.weblog.admin.model.vo.user.UpdateUserInfoReqVO;
import com.gaog.weblog.admin.model.vo.user.UpdateUserStatusReqVO;
import com.gaog.weblog.admin.model.vo.userinfo.UpdateAdminUserPasswordReqVO;
import com.gaog.weblog.common.utils.Response;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/21 23:15
 * @Version: 1.0
 * @Description: 管理后台服务
 */
public interface AdminUserService {
    /**
     * 修改密码
     *
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);


    /**
     * 获取当前登录用户信息
     * @return
     */
    Response findUserInfo();

    /**
     * 查询用户列表（分页）
     * @param findUserListReqVO
     * @return
     */
    Response findUserList(FindUserListReqVO findUserListReqVO);

    /**
     * 更新用户状态（启用/禁用）
     * @param updateUserStatusReqVO
     * @return
     */
    Response updateUserStatus(UpdateUserStatusReqVO updateUserStatusReqVO);

    /**
     * 创建用户
     * @param createUserReqVO
     * @return
     */
    Response createUser(CreateUserReqVO createUserReqVO);

    /**
     * 删除用户
     * @param deleteUserReqVO
     * @return
     */
    Response deleteUser(DeleteUserReqVO deleteUserReqVO);

    /**
     * Update user information
     * @param updateUserInfoReqVO
     * @return
     */
    Response updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO);

    /**
     * Get user roles
     * @param userId
     * @return
     */
    Response getUserRoles(Long userId);
}
