package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.user.CreateUserReqVO;
import com.gaog.weblog.admin.model.vo.user.DeleteUserReqVO;
import com.gaog.weblog.admin.model.vo.user.FindUserListReqVO;
import com.gaog.weblog.admin.model.vo.user.FindUserListRspVO;
import com.gaog.weblog.admin.model.vo.user.UpdateUserInfoReqVO;
import com.gaog.weblog.admin.model.vo.user.UpdateUserStatusReqVO;
import com.gaog.weblog.admin.model.vo.userinfo.FindUserInfoRspVO;
import com.gaog.weblog.admin.model.vo.userinfo.UpdateAdminUserPasswordReqVO;
import com.gaog.weblog.admin.service.AdminUserService;
import com.gaog.weblog.common.domain.dos.RoleDO;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.dos.UserRoleDO;
import com.gaog.weblog.common.domain.mapper.RoleMapper;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.domain.mapper.UserRoleMapper;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import com.gaog.weblog.jwt.model.CustomUserDetails;
import com.gaog.weblog.jwt.utils.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @Author: gaoge
 * @Date: 2025/11/21 23:16
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 修改密码
     *
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        //获取用户名和密码
        String username = updateAdminUserPasswordReqVO.getUsername();
        String oldPassword = updateAdminUserPasswordReqVO.getOldPassword();
        String password = updateAdminUserPasswordReqVO.getPassword();
        String confirmPassword = updateAdminUserPasswordReqVO.getConfirmPassword();


        // 通过用户名查询用户
        UserDO userDO = userMapper.findByUsername(username);

        if (userDO == null) {
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }

        // 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, userDO.getPassword())) {
            return Response.fail(ResponseCodeEnum.PASSWORD_ERROR);
        }

        // 验证新密码与确认密码是否一致
        if (!StringUtils.equals(password, confirmPassword)) {
            return Response.fail(ResponseCodeEnum.PASSWORD_NOT_SAME);
        }

        // 验证新密码与旧密码是否相同（使用原始密码比较）
        if (StringUtils.equals(password, oldPassword)) {
            return Response.fail(ResponseCodeEnum.NEW_NOT_SAME_FOR_OLD);
        }

        //加密密码
        String encodePassword = passwordEncoder.encode(password);

        //更新密码
        int count = userMapper.updatePasswordByUsername(username, encodePassword);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @Override
    public Response findUserInfo() {
        // 直接从 SecurityContext 中获取用户信息
        CustomUserDetails currentUser = SecurityContextUtil.getCurrentUser();

        if (currentUser == null) {
            return Response.fail("用户未登录");
        }

        return Response.success(FindUserInfoRspVO.builder()
                .username(currentUser.getUsername())
                .roles(new HashSet<>(currentUser.getRoles()))
                .build());
    }

    /**
     * 查询用户列表（分页）
     *
     * @param findUserListReqVO
     * @return
     */
    @Override
    public Response findUserList(FindUserListReqVO findUserListReqVO) {
        Long current = findUserListReqVO.getCurrent();
        Long size = findUserListReqVO.getSize();
        String username = findUserListReqVO.getUsername();
        Boolean isEnabled = findUserListReqVO.getIsEnabled();

        // 构建分页对象
        Page<UserDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), UserDO::getUsername, username)
                .eq(Objects.nonNull(isEnabled), UserDO::getIsEnabled, isEnabled)
                .eq(UserDO::getIsDeleted, false)
                .orderByDesc(UserDO::getCreateTime);

        //查询用户角色

        // ... existing code ...
        Page<UserDO> userPage = userMapper.selectPage(page, wrapper);

        // Convert to VO and query each user's roles
        List<FindUserListRspVO> userList = userPage.getRecords().stream()
                .map(userDO -> {
                    // Query user roles by userId
                    List<UserRoleDO> userRoles = userRoleMapper.selectByUserId(userDO.getId());
                    List<String> roleNames = null;
                    if (userRoles != null && !userRoles.isEmpty()) {
                        // Get role IDs and convert to role names
                        List<Long> roleIds = userRoles.stream()
                                .map(UserRoleDO::getRoleId)
                                .collect(Collectors.toList());

                        if (!CollectionUtils.isEmpty(roleIds)) {
                            List<RoleDO> roles = roleMapper.selectBatchIds(roleIds);
                            if (!CollectionUtils.isEmpty(roles)) {
                                roleNames = roles.stream()
                                        .map(RoleDO::getName)
                                        .collect(Collectors.toList());
                            }
                        }
                    }

                    return FindUserListRspVO.builder()
                            .id(userDO.getId())
                            .username(userDO.getUsername())
                            .nickname(userDO.getNickname())
                            .email(userDO.getEmail())
                            .avatar(userDO.getAvatar())
                            .isEnabled(userDO.getIsEnabled())
                            .roles(roleNames)
                            .createTime(userDO.getCreateTime())
                            .updateTime(userDO.getUpdateTime())
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponse.success(userPage, userList);
    }

    /**
     * 更新用户状态（启用/禁用）
     *
     * @param updateUserStatusReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateUserStatus(UpdateUserStatusReqVO updateUserStatusReqVO) {
        Long id = updateUserStatusReqVO.getId();
        Boolean isEnabled = updateUserStatusReqVO.getIsEnabled();

        // 1. 检查用户是否存在
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null || userDO.getIsDeleted()) {
            log.warn("更新用户状态失败，用户不存在: {}", id);
            return Response.fail(ResponseCodeEnum.USER_NOT_FOUND);
        }

        // 2. 更新用户状态
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserDO::getIsEnabled, isEnabled)
                .set(UserDO::getUpdateTime, LocalDateTime.now())
                .eq(UserDO::getId, id);

        int count = userMapper.update(null, updateWrapper);
        if (count != 1) {
            log.error("更新用户状态失败，数据库更新失败: {}", id);
            return Response.fail(ResponseCodeEnum.UPDATE_USER_STATUS_FAILED);
        }

        log.info("更新用户状态成功: userId={}, isEnabled={}", id, isEnabled);
        return Response.success();
    }

    /**
     * 创建用户
     *
     * @param createUserReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createUser(CreateUserReqVO createUserReqVO) {
        String username = createUserReqVO.getUsername();
        String password = createUserReqVO.getPassword();
        String email = createUserReqVO.getEmail();
        String nickname = createUserReqVO.getNickname();
        String introduction = createUserReqVO.getIntroduction();
        String avatar = createUserReqVO.getAvatar();
        Boolean isEnabled = createUserReqVO.getIsEnabled();
        List<Long> roleIds = createUserReqVO.getRoleIds();

        // 1. Check if username already exists
        UserDO existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            log.warn("Create user failed, username already exists: {}", username);
            return Response.fail(ResponseCodeEnum.USERNAME_ALREADY_EXISTS);
        }

        // 2. Encode password
        String encodedPassword = passwordEncoder.encode(password);

        // 3. Build user entity
        UserDO userDO = UserDO.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .nickname(nickname)
                .introduction(introduction)
                .avatar(avatar)
                .isEnabled(isEnabled)
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();

        // 4. Insert user to database
        int count = userMapper.insert(userDO);
        if (count != 1) {
            log.error("Create user failed, database insert failed: {}", username);
            return Response.fail(ResponseCodeEnum.CREATE_USER_FAILED);
        }

        // 5. Assign roles if provided
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                UserRoleDO userRoleDO = UserRoleDO.builder()
                        .userId(userDO.getId())
                        .roleId(roleId)
                        .createTime(new Date())
                        .build();
                userRoleMapper.insert(userRoleDO);
            }
        }

        log.info("Create user successfully: username={}, isEnabled={}", username, isEnabled);
        return Response.success();
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param deleteUserReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteUser(DeleteUserReqVO deleteUserReqVO) {
        Long id = deleteUserReqVO.getId();

        // 1. 检查用户是否存在
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null || userDO.getIsDeleted()) {
            log.warn("删除用户失败，用户不存在: {}", id);
            return Response.fail(ResponseCodeEnum.USER_NOT_FOUND);
        }

        // 2. 检查是否为当前登录用户（防止自己删除自己）
        CustomUserDetails currentUser = SecurityContextUtil.getCurrentUser();
        if (currentUser != null && currentUser.getUsername().equals(userDO.getUsername())) {
            log.warn("删除用户失败，不能删除当前登录用户: {}", userDO.getUsername());
            return Response.fail(ResponseCodeEnum.CANNOT_DELETE_CURRENT_USER);
        }

        // 3. 逻辑删除用户
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserDO::getIsDeleted, true)
                .set(UserDO::getUpdateTime, LocalDateTime.now())
                .eq(UserDO::getId, id);

        int count = userMapper.update(null, updateWrapper);
        if (count != 1) {
            log.error("删除用户失败，数据库更新失败: {}", id);
            return Response.fail(ResponseCodeEnum.DELETE_USER_FAILED);
        }

        log.info("删除用户成功: userId={}, username={}", id, userDO.getUsername());
        return Response.success();
    }

    /**
     * 更新用户信息
     *
     * @param updateUserInfoReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO) {
        Long id = updateUserInfoReqVO.getId();
        String email = updateUserInfoReqVO.getEmail();
        String username = updateUserInfoReqVO.getUsername();
        String password = updateUserInfoReqVO.getPassword();
        String nickname = updateUserInfoReqVO.getNickname();
        String introduction = updateUserInfoReqVO.getIntroduction();
        String avatar = updateUserInfoReqVO.getAvatar();
        String githubUrl = updateUserInfoReqVO.getGithubUrl();
        String twitterUrl = updateUserInfoReqVO.getTwitterUrl();
        String weiboUrl = updateUserInfoReqVO.getWeiboUrl();

        // 1. 检查用户是否存在
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null || userDO.getIsDeleted()) {
            log.warn("更新用户信息失败，用户不存在: {}", id);
            return Response.fail(ResponseCodeEnum.USER_NOT_FOUND);
        }

        //加密密码
        if (StringUtils.isNotBlank(password)) {
            password = passwordEncoder.encode(password);
        }

        // 2. 更新用户信息（只更新非空字段）
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(StringUtils.isNotBlank(email), UserDO::getEmail, email)
                .set(StringUtils.isNotBlank(username), UserDO::getUsername, username)
                .set(StringUtils.isNotBlank(password), UserDO::getPassword, password)
                .set(StringUtils.isNotBlank(nickname), UserDO::getNickname, nickname)
                .set(StringUtils.isNotBlank(introduction), UserDO::getIntroduction, introduction)
                .set(StringUtils.isNotBlank(avatar), UserDO::getAvatar, avatar)
                .set(StringUtils.isNotBlank(githubUrl), UserDO::getGithubUrl, githubUrl)
                .set(StringUtils.isNotBlank(twitterUrl), UserDO::getTwitterUrl, twitterUrl)
                .set(StringUtils.isNotBlank(weiboUrl), UserDO::getWeiboUrl, weiboUrl)
                .set(UserDO::getUpdateTime, LocalDateTime.now())
                .eq(UserDO::getId, id);

        int count = userMapper.update(null, updateWrapper);
        if (count != 1) {
            log.error("更新用户信息失败，数据库更新失败: {}", id);
            return Response.fail(ResponseCodeEnum.UPDATE_USER_INFO_FAILED);
        }

        log.info("更新用户信息成功: userId={}", id);
        return Response.success();
    }

    /**
     * Get user roles
     *
     * @param userId
     * @return
     */
    @Override
    public Response getUserRoles(Long userId) {
        // Check if user exists
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO) || userDO.getIsDeleted()) {
            log.warn("User not found: {}", userId);
            return Response.fail(ResponseCodeEnum.USER_NOT_FOUND);
        }

        // Query user roles by userId
        List<UserRoleDO> userRoles = userRoleMapper.selectByUserId(userId);

        List<String> roleNames = null;
        if (!CollectionUtils.isEmpty(userRoles)) {
            // Get role IDs and convert to role names
            List<Long> roleIds = userRoles.stream()
                    .map(UserRoleDO::getRoleId)
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(roleIds)) {
                List<RoleDO> roles = roleMapper.selectBatchIds(roleIds);
                if (!CollectionUtils.isEmpty(roles)) {
                    roleNames = roles.stream()
                            .map(RoleDO::getName)
                            .collect(Collectors.toList());
                }
            }
        }

        return Response.success(roleNames);
    }
}
