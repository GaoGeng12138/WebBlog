package com.gaog.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaog.weblog.admin.model.vo.role.AddRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.AssignRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.DeleteRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRolePageListReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRolePageListRspVO;
import com.gaog.weblog.admin.model.vo.role.FindRoleSelectListReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRoleSelectListRspVO;
import com.gaog.weblog.admin.model.vo.role.UpdateRoleReqVO;
import com.gaog.weblog.admin.service.AdminRoleService;
import com.gaog.weblog.common.domain.dos.RoleDO;
import com.gaog.weblog.common.domain.dos.UserDO;
import com.gaog.weblog.common.domain.dos.UserRoleDO;
import com.gaog.weblog.common.domain.mapper.RoleMapper;
import com.gaog.weblog.common.domain.mapper.UserMapper;
import com.gaog.weblog.common.domain.mapper.UserRoleMapper;
import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.utils.PageResponse;
import com.gaog.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Role management service implementation
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 * @Description: Implementation of role management service
 */
@Service
@Slf4j
public class AdminRoleServiceImpl implements AdminRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * Create a new role
     *
     * @param addRoleReqVO add role request VO
     * @return Response
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addRole(AddRoleReqVO addRoleReqVO) {
        String roleName = addRoleReqVO.getName();

        // Check if role name already exists
        RoleDO existRole = roleMapper.selectByName(roleName);
        if (Objects.nonNull(existRole)) {
            log.warn("Role name already exists: {}", roleName);
            return Response.fail(ResponseCodeEnum.ROLE_NAME_ALREADY_EXISTS);
        }

        // Build DO object
        RoleDO insertRoleDO = RoleDO.builder()
                .name(roleName)
                .description(addRoleReqVO.getDescription())
                .isEnabled(addRoleReqVO.getIsEnabled())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Insert role
        int count = roleMapper.insert(insertRoleDO);
        if (count != 1) {
            log.error("Failed to create role: {}", roleName);
            return Response.fail(ResponseCodeEnum.CREATE_ROLE_FAILED);
        }

        log.info("Role created successfully: {}", roleName);
        return Response.success();
    }

    /**
     * Query role page list
     *
     * @param findRolePageListReqVO find role page list request VO
     * @return Response
     */
    @Override
    public Response findRolePageList(FindRolePageListReqVO findRolePageListReqVO) {
        Long current = findRolePageListReqVO.getCurrent();
        Long size = findRolePageListReqVO.getSize();
        String name = findRolePageListReqVO.getName();
        Boolean isEnabled = findRolePageListReqVO.getIsEnabled();

        // Build pagination object
        Page<RoleDO> page = new Page<>(current, size);

        // Build query conditions
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), RoleDO::getName, name)
               .eq(Objects.nonNull(isEnabled), RoleDO::getIsEnabled, isEnabled)
               .eq(RoleDO::getIsDeleted, false)
               .orderByAsc(RoleDO::getId);

        // Execute pagination query
        Page<RoleDO> rolePage = roleMapper.selectPage(page, wrapper);

        // Convert to VO object
        List<FindRolePageListRspVO> roleList = null;
        if (!CollectionUtils.isEmpty(rolePage.getRecords())) {
            roleList = rolePage.getRecords().stream()
                    .map(roleDO -> FindRolePageListRspVO.builder()
                            .id(roleDO.getId())
                            .name(roleDO.getName())
                            .description(roleDO.getDescription())
                            .isEnabled(roleDO.getIsEnabled())
                            .createTime(roleDO.getCreateTime())
                            .updateTime(roleDO.getUpdateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(rolePage, roleList);
    }

    /**
     * Update role information
     *
     * @param updateRoleReqVO update role request VO
     * @return Response
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateRole(UpdateRoleReqVO updateRoleReqVO) {
        Long roleId = updateRoleReqVO.getId();
        String description = updateRoleReqVO.getDescription();
        Boolean isEnabled = updateRoleReqVO.getIsEnabled();

        // Check if role exists
        RoleDO roleDO = roleMapper.selectByIdNotDeleted(roleId);
        if (Objects.isNull(roleDO)) {
            log.warn("Role not found: {}", roleId);
            return Response.fail(ResponseCodeEnum.ROLE_NOT_FOUND);
        }

        // Update role information
        LambdaUpdateWrapper<RoleDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(StringUtils.isNotBlank(description), RoleDO::getDescription, description)
                .set(Objects.nonNull(isEnabled), RoleDO::getIsEnabled, isEnabled)
                .set(RoleDO::getUpdateTime, LocalDateTime.now())
                .eq(RoleDO::getId, roleId);

        int count = roleMapper.update(null, updateWrapper);
        if (count != 1) {
            log.error("Failed to update role: {}", roleId);
            return Response.fail(ResponseCodeEnum.UPDATE_ROLE_FAILED);
        }

        log.info("Role updated successfully: roleId={}", roleId);
        return Response.success();
    }

    /**
     * Delete role (logical delete)
     *
     * @param deleteRoleReqVO delete role request VO
     * @return Response
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteRole(DeleteRoleReqVO deleteRoleReqVO) {
        Long roleId = deleteRoleReqVO.getId();

        // Check if role exists
        RoleDO roleDO = roleMapper.selectByIdNotDeleted(roleId);
        if (Objects.isNull(roleDO)) {
            log.warn("Role not found: {}", roleId);
            return Response.fail(ResponseCodeEnum.ROLE_NOT_FOUND);
        }

        // Logical delete role
        LambdaUpdateWrapper<RoleDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(RoleDO::getIsDeleted, true)
                .set(RoleDO::getUpdateTime, LocalDateTime.now())
                .eq(RoleDO::getId, roleId);

        int count = roleMapper.update(null, updateWrapper);
        if (count != 1) {
            log.error("Failed to delete role: {}", roleId);
            return Response.fail(ResponseCodeEnum.DELETE_ROLE_FAILED);
        }

        log.info("Role deleted successfully: roleId={}", roleId);
        return Response.success();
    }

    /**
     * Query role select list (all enabled roles)
     *
     * @param findRoleSelectListReqVO find role select list request VO
     * @return Response
     */
    @Override
    public Response findRoleSelectList(FindRoleSelectListReqVO findRoleSelectListReqVO) {
        // Query all enabled roles
        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleDO::getIsEnabled, true)
               .eq(RoleDO::getIsDeleted, false)
               .orderByAsc(RoleDO::getId);

        List<RoleDO> roleList = roleMapper.selectList(wrapper);

        List<FindRoleSelectListRspVO> resultList = null;
        if (!CollectionUtils.isEmpty(roleList)) {
            resultList = roleList.stream()
                    .map(roleDO -> FindRoleSelectListRspVO.builder()
                            .id(roleDO.getId())
                            .name(roleDO.getName())
                            .description(roleDO.getDescription())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(resultList);
    }

    /**
     * Assign roles to user
     *
     * @param assignRoleReqVO assign role request VO
     * @return Response
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response assignRole(AssignRoleReqVO assignRoleReqVO) {
        Long userId = assignRoleReqVO.getUserId();
        List<Long> roleIds = assignRoleReqVO.getRoleIds();

        // Check if user exists
        UserDO userDO = userMapper.selectById(userId);
        if (Objects.isNull(userDO) || userDO.getIsDeleted()) {
            log.warn("User not found: {}", userId);
            return Response.fail(ResponseCodeEnum.USER_NOT_FOUND);
        }

        // Delete existing user roles
        userRoleMapper.deleteByUserId(userId);

        // Add new roles to user
        if (!CollectionUtils.isEmpty(roleIds)) {
            // Get all role objects to validate they exist
            List<RoleDO> roles = roleMapper.selectBatchIds(roleIds);
            if (roles.size() != roleIds.size()) {
                log.warn("Some roles not found for user: {}", userId);
                return Response.fail(ResponseCodeEnum.ROLE_NOT_FOUND);
            }

            // Create user role relationships
            for (RoleDO roleDO : roles) {
                UserRoleDO userRoleDO = UserRoleDO.builder()
                        .userId(userId)
                        .roleId(roleDO.getId())
                        .createTime(new java.util.Date())
                        .build();
                userRoleMapper.insert(userRoleDO);
            }
        }

        log.info("Roles assigned successfully: userId={}, roleCount={}", userId, roleIds.size());
        return Response.success();
    }
}
